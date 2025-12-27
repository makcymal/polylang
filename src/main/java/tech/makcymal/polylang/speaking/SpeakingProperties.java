package tech.makcymal.polylang.speaking;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import tech.makcymal.polylang.speaking.transcribe.models.Model;

import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.time.Duration;

@Slf4j
@Data
@Component
@ConfigurationProperties("speaking")
public class SpeakingProperties {

    @NotNull
    private String speechRecordsDir;

    @NotNull
    private String speechTranscriptionsDir;

    @NotNull
    private Duration transcribeBySnippetsOfDuration;

    @NotNull
    private String whisperPath;

    @NotNull
    private Model whisperModel;

    @NotNull
    private String whisperModelsDir;

    private int whisperThreadPoolSize;

    @NotNull
    private Duration waitingWhisperResponseTimeout;

    private float detectSilenceByNoSpeechProbThreshold;

    @NotNull
    private Duration silenceTimeoutToStopRecording;

}
