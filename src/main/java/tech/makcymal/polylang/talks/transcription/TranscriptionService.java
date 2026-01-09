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
        tasks.forEach(task -> cmd.add(task.getFileToTranscribe()));

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

        log.info("Newly transcribed words: " + words);

        updateTranscribedWords(task.getTalkId(), words);

        String transcribedText = transcribedWords.get(task.getTalkId()).stream()
                .map(Word::getWord)
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
        transcribedWords.computeIfAbsent(talkId, k -> new ArrayList<>());
        List<Word> allWords = transcribedWords.get(talkId);

        if (allWords.isEmpty() || newWords.isEmpty()) {
            allWords.addAll(newWords);
            return;
        }

        int i = findFirst(newWords, word -> word.getEnd() >= allWords.getLast().getStart());
        while (!allWords.isEmpty() && newWords.get(i).getStart() < allWords.getLast().getEnd()) {
            allWords.removeLast();
        }
        allWords.addAll(newWords);
    }

}
