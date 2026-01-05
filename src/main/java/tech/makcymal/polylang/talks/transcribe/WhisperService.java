package tech.makcymal.polylang.talks.transcribe;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.common.NamingThreadFactory;
import tech.makcymal.polylang.common.SystemUtils;
import tech.makcymal.polylang.talks.TalksProperties;
import tech.makcymal.polylang.talks.transcribe.models.Request;
import tech.makcymal.polylang.talks.transcribe.models.Response;
import tech.makcymal.polylang.talks.transcribe.models.Result;

import jakarta.annotation.PostConstruct;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhisperService {

    private final TalksProperties speakingProps;
    private final RequestSet requests;
    private final ResultSet results;

    private List<String> rawCmd;
    private int snippetDuration;
    private ExecutorService executor;

    @PostConstruct
    public void init() {
        log.info("Running WhisperService.init...");

        rawCmd = List.of(
                speakingProps.getWhisperPath(),
                "--model_dir", speakingProps.getWhisperModelsDir(),
                "--model", speakingProps.getWhisperModel().getName(),
                "-o", speakingProps.getSpeechTranscriptionsDir(),
                "-f", "json",
                "--verbose", "False",
                "--language", "en",
                "--fp16", "False",
                "--word_timestamps", "True",
                "--clip_timestamps"
        );

        snippetDuration = (int) speakingProps.getTranscribeBySnippetsOfDuration().getSeconds();

        executor = Executors.newFixedThreadPool(
                speakingProps.getWhisperThreadPoolSize(),
                new NamingThreadFactory("whisper-")
        );

        log.info("Running WhisperService.init...DONE");
    }

    public Result transcribe(@NonNull String recordFilePath, int snippetIdx) {
        log.info("Running WhisperService.transcribe...");

        requests.put(recordFilePath, snippetIdx);
        Result result = results.getWhenAdded(recordFilePath);

        log.info("Running WhisperService.transcribe...DONE");

        return result;
    }

    @Scheduled(fixedRateString = "${talks.transcribe-by-snippets-of-duration}")
    private void scheduleTranscription() {
        // log.info("Running WhisperService.scheduleTranscription...");

        for (int snippetIdx = 0; snippetIdx < requests.size(); snippetIdx++) {
            final int finalSnippetIdx = snippetIdx;
            executor.submit(() -> executeWhisperAndReadResponses(finalSnippetIdx));
        }

        // log.info("Running WhisperService.scheduleTranscription...DONE");
    }

    private void executeWhisperAndReadResponses(int snippetIdx) {
        log.info("Running WhisperService.executeWhisperAndReadResponses...");

        Optional<List<Request>> requestList = requests.getAll(snippetIdx);

        if (requestList.isEmpty()) {
            return;
        }

        String start = String.format("%d", snippetDuration * snippetIdx);
        List<String> recordFilePaths = requestList.get().stream()
                .map(Request::getRecordFilePath)
                .toList();

        Optional<Throwable> executionError = Optional.empty();
        try {
            executeWhisper(start, recordFilePaths);
        } catch (RuntimeException e) {
            executionError = Optional.of(e);
        }

        for (int i = 0; i < requestList.get().size(); i++) {
            Request request = requestList.get().get(i);
            String filename = request.getRecordFilePath();

            Response response = null;
            Optional<Throwable> readingError = Optional.empty();
            try {
                response = readWhisperResponse(filename);
            } catch (RuntimeException e) {
                readingError = Optional.of(e);
            }

            Result result = new Result(request, Optional.ofNullable(response), executionError, readingError);
            results.add(filename, result);
        }

        log.info("Running WhisperService.executeWhisperAndReadResponses...DONE");
    }

    private void executeWhisper(@NonNull String clipTimestamps, @NonNull List<String> filenames) {
        log.info("Running WhisperService.executeWhisper...");

        List<String> cmd = new ArrayList<>(rawCmd);
        cmd.add(clipTimestamps);
        cmd.addAll(filenames);
        SystemUtils.executeCommand(cmd);

        log.info("Running WhisperService.executeWhisper...DONE");
    }

    private Response readWhisperResponse(@NonNull String recordFilePath) {
        log.info("Running WhisperService.readWhisperResponse...");

        String fileNameWithoutExt = SystemUtils.getFileNameWithoutExtension(Paths.get(recordFilePath).getFileName().toString());
        String transcriptionFilePath = speakingProps.getSpeechTranscriptionsDir() + fileNameWithoutExt + ".json";

        log.info("Running WhisperService.readWhisperResponse...DONE");

        return SystemUtils.readJsonFile(transcriptionFilePath, Response.class);
    }

    public void cleanup(@NonNull String filename) {
        results.cleanup(filename);
    }

}
