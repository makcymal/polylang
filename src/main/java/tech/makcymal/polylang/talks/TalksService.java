package tech.makcymal.polylang.talks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tech.makcymal.polylang.common.exceptions.HttpException;
import tech.makcymal.polylang.talks.transcription.Task;
import tech.makcymal.polylang.talks.transcription.TranscriptionService;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import static tech.makcymal.polylang.common.CommonUtils.mkdir;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalksService {

    private final TalksRepo repo;
    private final TalksProperties props;
    private final TranscriptionService transcriptionService;

    @PostConstruct
    public void init() {
        mkdir(props.getRecordsDir());
        mkdir(props.getTranscriptionsDir());
    }

    public UUID createNewTalk(UUID textId, UUID userId, UUID clientId) {
        TalkEntity entity = TalkEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .textId(textId)
                .clientId(clientId)
                .build();
        TalkEntity savedEntity = repo.save(entity);
        return savedEntity.getId();
    }

    public void submitTranscriptionTask(UUID talkId, int chunkStart, InputStream chunkStream) {
        Task task = new Task(
                talkId,
                (float) chunkStart / 1000,
                "%s/%s.%d.webm".formatted(props.getRecordsDir(), talkId.toString(), chunkStart),
                "%s/%s.%d.json".formatted(props.getTranscriptionsDir(), talkId.toString(), chunkStart)
        );

        try {
            Files.copy(
                    chunkStream,
                    Path.of(task.getFileToTranscribe()),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException("err - writing audio/webm InputStream to file", e);
        }

        transcriptionService.submitTranscribingTask(task);
    }

    public String getTranscription(UUID talkId) {
        return repo.findById(talkId)
                .orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, "talk with given id not found"))
                .getTranscription();
    }

    @Scheduled(fixedRateString = "${talks.cleanup-rate}")
    public void cleanup() {
        repo.deleteOldWithEmptyTranscription();
    }

}
