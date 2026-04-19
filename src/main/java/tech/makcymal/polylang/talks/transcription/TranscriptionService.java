package tech.makcymal.polylang.talks.transcription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.common.NamingThreadFactory;
import tech.makcymal.polylang.common.CommonUtils;
import tech.makcymal.polylang.talks.TalksProperties;
import tech.makcymal.polylang.talks.TalksRepo;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static tech.makcymal.polylang.talks.transcription.WhisperOutput.Word;
import static tech.makcymal.polylang.common.CommonUtils.findFirst;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final TalksRepo repo;
    private final TalksProperties props;

    // transcribing stage
    ScheduledExecutorService transcribingExecutor;
    private List<Task> transcribeTasks = new ArrayList<>();
    private final Lock transcribeTasksLock = new ReentrantLock();
    private List<String> rawTranscribeCmd;

    // processing stage
    ExecutorService processingExecutor;
    private final BlockingQueue<Runnable> processTasks = new LinkedBlockingQueue<>();
    private final Map<UUID, List<Word>> transcribedWords = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        initTranscribingExecutor();
        initProcessingExecutor();
    }

    // transcribing stage

    void initTranscribingExecutor() {
        rawTranscribeCmd = List.of(
                props.getWhisperPath(),
                "--model_dir", props.getWhisperModelsDir(),
                "--model", props.getWhisperModel().getName(),
                "-o", props.getTranscriptionsDir(),
                "-f", "json",
                "--verbose", "False",
                "--lang", "en",
                "--fp16", "False",
                "--word_timestamps", "True"
        );

        int executorSize = props.getTranscribingThreadPoolSize();

        transcribingExecutor = Executors.newScheduledThreadPool(
                executorSize,
                new NamingThreadFactory("transcribing-")
        );

        long period = props.getTranscribingPeriod().toMillis();
        long delayStep = Math.floorDiv(period, executorSize);

        for (long i = 0; i < executorSize; i++) {
            transcribingExecutor.scheduleAtFixedRate(this::transcribe, delayStep * i, period, TimeUnit.MILLISECONDS);
        }
    }

    void transcribe() {
        List<Task> tasks = takeTranscribeTasks();
        if (tasks.isEmpty()) {
            return;
        }

        List<String> cmd = new ArrayList<>(rawTranscribeCmd.size() + tasks.size());
        cmd.addAll(rawTranscribeCmd);
        tasks.forEach(task -> {
            log.info("Transcribing {}", task.getFileToTranscribe());
            cmd.add(task.getFileToTranscribe());
        });

        try {
            CommonUtils.executeCommand(cmd);
        } catch (RuntimeException e) {
            log.error("err - executing whisper", e);
            tasks.forEach(task -> task.setTranscribingError(e));
        }

        submitProcessingTasks(tasks);
    }

    List<Task> takeTranscribeTasks() {
        transcribeTasksLock.lock();
        List<Task> result = transcribeTasks;
        this.transcribeTasks = new ArrayList<>();
        transcribeTasksLock.unlock();
        return result;
    }

    public void submitTranscribingTask(Task task) {
        transcribeTasksLock.lock();
        if (transcribeTasks == null) {
            transcribeTasks = new ArrayList<>();
        }
        transcribeTasks.add(task);
        transcribeTasksLock.unlock();
    }

    // processing stage

    void initProcessingExecutor() {
        int executorSize = props.getTranscriptionProcessingThreadPoolSize();

        processingExecutor = new ThreadPoolExecutor(
                executorSize,
                executorSize,
                0,
                TimeUnit.MILLISECONDS,
                processTasks,
                new NamingThreadFactory("transcription-processing-")
        );
    }

    void submitProcessingTasks(List<Task> tasks) {
        tasks.forEach(task -> {
            processingExecutor.execute(() -> processTranscription(task));
        });
    }

    void processTranscription(Task task) {
        WhisperOutput output = CommonUtils.readJsonFile(task.getFileToProcess(), WhisperOutput.class);
        List<Word> words = output.getSegments().stream()
                .map(WhisperOutput.Segment::getWords)
                .flatMap(List::stream)
                .peek(word -> word.moveLater(task.getChunkStart()))
                .toList();

        // log.info("Newly transcribed words: {} starting at: {}", words, task.getChunkStart());

        updateTranscribedWords(task.getTalkId(), words);

        String transcribedText = transcribedWords.get(task.getTalkId()).stream()
                .map(Word::getWord)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "))
                // remove spaces at the beginning
                .replace("^\\s*", "")
                // remove spaces at the end
                .replace("\\s*$", "")
                // remove spaces before dots
                .replace("\\s*\\.", ".")
                // remove spaces before commas
                .replace("\\s*\\,", ",")
                // remove newlines
                .replace("\n", " ")
                // reduce consecutive spaces to one
                .replace("\\s*", " ");

        repo.setTranscription(task.getTalkId(), transcribedText);
    }

    void updateTranscribedWords(UUID talkId, List<Word> newWords) {
        transcribedWords.computeIfAbsent(talkId, _ -> new ArrayList<>());
        List<Word> oldWords = transcribedWords.get(talkId);

        if (oldWords.isEmpty() || newWords.isEmpty()) {
            oldWords.addAll(newWords);
            return;
        }

        List<Word> mergedWords = mergeOldAndNewWords(oldWords, newWords);
        log.info("\n{}\n+\n{}\n=\n{}", oldWords, newWords, mergedWords);

        transcribedWords.put(talkId, mergedWords);
    }

    private List<Word> mergeOldAndNewWords(List<Word> oldWords, List<Word> newWords) {
        // common subsequence length
        int[][] csl = new int[newWords.size() + 1][oldWords.size() + 1];
        int pivots = 0;
        List<Integer> newWordsPivots = new ArrayList<>();
        List<Integer> oldWordsPivots = new ArrayList<>();

        for (int i = 0; i < newWords.size(); i++) {
            for (int j = 0; j < oldWords.size(); j++) {
                if (newWords.get(i).equalsWithProb(oldWords.get(j), 0.5f)) {
                    csl[i + 1][j + 1] = csl[i][j] + 1;
                    pivots++;
                    newWordsPivots.add(i);
                    oldWordsPivots.add(j);
                } else {
                    csl[i + 1][j + 1] = Math.max(csl[i][j + 1], csl[i + 1][j]);
                }
            }
        }

        if (pivots == 0) {
            return mergeOldAndNewWordsWithoutPivots(oldWords, newWords);
        }

        List<Word> merged = new ArrayList<>(Math.min(newWords.size(), oldWords.size()));

        merged.addAll(oldWords.subList(0, oldWordsPivots.getFirst() + 1));

        for (int pv = 1; pv < pivots; pv++) {
            float newWordsProb = 0;
            int start = newWordsPivots.get(pv - 1) + 1;
            int end = newWordsPivots.get(pv);
            for (int i = start; i < end; ++i) {
                newWordsProb += newWords.get(i).getProbability();
            }
            if (start != end) {
                newWordsProb /= end - start;
            }

            float oldWordsProb = 0;
            start = oldWordsPivots.get(pv - 1) + 1;
            end = oldWordsPivots.get(pv);
            for (int j = start; j < end; ++j) {
                oldWordsProb += oldWords.get(j).getProbability();
            }
            if (start != end) {
                oldWordsProb /= end - start;
            }

            if (newWordsProb > oldWordsProb) {
                merged.addAll(newWords.subList(newWordsPivots.get(pv - 1) + 1, newWordsPivots.get(pv) + 1));
            } else {
                merged.addAll(oldWords.subList(oldWordsPivots.get(pv - 1) + 1, oldWordsPivots.get(pv) + 1));
            }
        }

        merged.addAll(newWords.subList(newWordsPivots.getLast() + 1, newWords.size()));

        return merged;
    }

    private List<Word> mergeOldAndNewWordsWithoutPivots(List<Word> oldWords, List<Word> newWords) {
        int newWordsStart = findFirst(newWords, w -> w.getProbability() > 0.7f);
        if (newWordsStart == -1 || newWordsStart == newWords.size()) {
            return oldWords;
        }

        int oldWordsEnd = findFirst(oldWords, w -> newWords.get(newWordsStart).getStart() < w.getEnd());

        List<Word> merged = new ArrayList<>(oldWordsEnd + newWords.size() - newWordsStart);
        merged.addAll(oldWords.subList(0, oldWordsEnd));
        merged.addAll(newWords.subList(newWordsStart, newWords.size()));

        return merged;
    }

}
