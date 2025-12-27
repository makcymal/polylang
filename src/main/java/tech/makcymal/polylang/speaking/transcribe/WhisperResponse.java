package tech.makcymal.polylang.speaking.transcribe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhisperResponse {

    private String text;
    private List<Segment> segments;
    private String language;

    @Data
    @Builder
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

    @Data
    @Builder
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
