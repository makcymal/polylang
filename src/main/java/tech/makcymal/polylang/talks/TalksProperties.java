package tech.makcymal.polylang.talks;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import tech.makcymal.polylang.talks.transcribe.models.Model;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;

@Slf4j
@Data
@Component
@ConfigurationProperties("talks")
public class TalksProperties {

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
