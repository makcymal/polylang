package tech.makcymal.polylang.talks.transcription;

import lombok.Data;

import java.util.UUID;

@Data
public class Task {

    private UUID talkId;
    private float chunkStart;
    private String fileToTranscribe;
    private String fileToProcess;
    private Throwable transcribingError;
    private Throwable processingError;

    public Task(UUID talkId, float chunkStart, String fileToTranscribe, String fileToProcess) {
        this.talkId = talkId;
        this.chunkStart = chunkStart;
        this.fileToTranscribe = fileToTranscribe;
        this.fileToProcess = fileToProcess;
    }

}
