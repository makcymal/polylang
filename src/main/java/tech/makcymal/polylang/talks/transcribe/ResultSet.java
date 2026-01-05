package tech.makcymal.polylang.talks.transcribe;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.talks.TalksProperties;
import tech.makcymal.polylang.talks.transcribe.models.Result;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultSet {

    private final TalksProperties speakingProps;

    private final Map<String, CompletableFuture<Result>> results = new ConcurrentHashMap<>();

    public Result getWhenAdded(@NonNull String filePath) {
        log.info("Running ResultSet.getWhenAdded...");

        if (!results.containsKey(filePath)) {
            results.put(filePath, new CompletableFuture<>());
        }

        Result result;

        try {
            result = results.get(filePath).get(speakingProps.getWaitingWhisperResponseTimeout().getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {

            String msg = String.format(
                    "err - waiting on whisper response interrupted: filePath: [%s], msg: [%s]",
                    filePath,
                    e.getMessage()
            );
            log.error(msg, e);
            throw new RuntimeException(e);

        } catch (TimeoutException e) {

            String msg = String.format(
                    "err - waiting on whisper response timed out: filePath: [%s], msg: [%s]",
                    filePath,
                    e.getMessage()
            );
            log.error(msg, e);
            throw new RuntimeException(e);

        } finally {
            log.info("Running ResultSet.getWhenAdded...DONE");
        }

        return result;
    }

    public void add(@NonNull String filePath, @NonNull Result result) {
        log.info("Running ResultSet.add...");

        if (!results.containsKey(filePath)) {
            results.put(filePath, new CompletableFuture<>());
        }
        results.get(filePath).complete(result);

        log.info("Running ResultSet.add...DONE");
    }

    public void cleanup(@NonNull String request) {
        results.remove(request);
    }

}
