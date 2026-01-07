package tech.makcymal.polylang.talks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import tech.makcymal.polylang.talks.transcription.WhisperModel;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;

@Slf4j
@Data
@Component
@ConfigurationProperties("talks")
public class TalksProperties {

    @NotNull
    private Duration cleanupRate;

    @NotNull
    private String recordsDir;

    @NotNull
    private String transcriptionsDir;

    @NotNull
    private String whisperPath;

    @NotNull
    private WhisperModel whisperModel;

    @NotNull
    private String whisperModelsDir;

    @NotNull
    private Duration whisperResponseTimeout;

    private int transcribingThreadPoolSize;
    private int transcriptionProcessingThreadPoolSize;

    @NotNull
    private Duration transcribingPeriod;

    private float detectSilenceByNoSpeechProbThreshold;

    @NotNull
    private Duration silenceTimeoutToStopRecording;

}
