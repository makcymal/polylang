package tech.makcymal.polylang.talks.transcription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.common.NamingThreadFactory;
import tech.makcymal.polylang.common.SystemUtils;
import tech.makcymal.polylang.talks.TalksProperties;
import tech.makcymal.polylang.talks.TalksRepo;

import jakarta.annotation.PostConstruct;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final TalksRepo repo;
    private final TalksProperties props;
    private final Map<UUID, List<Word>> transcriptions = new ConcurrentHashMap<>();

    // transcribing stage
    ScheduledExecutorService transcribingExecutor;
    private List<Task> transcribeTasks = new ArrayList<>();
    private final Lock transcribeTasksLock = new ReentrantLock();
    private List<String> rawTranscribeCmd;

    // processing stage
    ExecutorService processingExecutor;
    private final BlockingQueue<Runnable> processTasks = new LinkedBlockingQueue<>();

    // saving stage
    ExecutorService savingExecutor;
    private final BlockingQueue<Runnable> savingTasks = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        initTranscribingExecutor();
        initProcessingExecutor();
        initSavingExecutor();
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
                "--language", "en",
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
            SystemUtils.executeCommand(cmd);
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
        WhisperOutput output = SystemUtils.readJsonFile(task.getFileToProcess(), WhisperOutput.class);
        List<Word> words = output.getSegments().stream()
                .map(WhisperOutput.Segment::getWords)
                .flatMap(List::stream)
                .toList();
        transcriptions.put(task.getTalkId(), words);
        submitSavingTask(task);
    }

    // saving stage

    void initSavingExecutor() {
        int executorSize = props.getTranscriptionSavingThreadPoolSize();

        savingExecutor = new ThreadPoolExecutor(
                executorSize,
                executorSize,
                0,
                TimeUnit.MILLISECONDS,
                savingTasks,
                new NamingThreadFactory("transcription-processing-")
        );
    }

    void submitSavingTask(Task task) {
        savingExecutor.execute(() -> saveTranscription(task));
    }

    void saveTranscription(Task task) {
        List<Word> words = transcriptions.get(task.getTalkId());
        String transcription = words.stream().map(Word::getWord).collect(Collectors.joining(" "));
        repo.setTranscription(task.getTalkId(), transcription);
    }

}
