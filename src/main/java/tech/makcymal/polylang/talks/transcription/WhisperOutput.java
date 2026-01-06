package tech.makcymal.polylang.talks.transcription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhisperOutput {

    private String text;
    private List<Segment> segments;
    private String language;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Segment {

        private String text;
        private Float start;
        private Float end;

        @JsonProperty("no_speech_prob")
        private Float noSpeechProb;

        private List<Word> words;

        // ignored fields: id, seek, tokens, temperature, avg_logprob, compression_ratio

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Word {

        private String word;
        private Float start;
        private Float end;
        private Float probability;

    }

}
