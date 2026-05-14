package tech.makcymal.polylang.talks.transcription;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.function.Predicate;

import static tech.makcymal.polylang.common.CommonUtils.findFirst;
import static tech.makcymal.polylang.common.CommonUtils.findNext;

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
        private float start;
        private float end;

        @JsonProperty("no_speech_prob")
        private float noSpeechProb;

        private List<Word> words;

        // ignored fields: id, seek, tokens, temperature, avg_logprob, compression_ratio

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Word {

        private static final Predicate<Character> ALPHA_NUMERIC_FILTER = c -> Character.isAlphabetic(c) || Character.isDigit(c);

        private String word;
        private float start;
        private float end;
        private float probability;

        public void moveLater(float diff) {
            this.start += diff;
            this.end += diff;
        }

        @Override
        public String toString() {
            return "[%s][%.2f:%.2f~%.2f]".formatted(word, start, end, probability);
        }

        public boolean equalsWithProb(Word o, float probThreshold, float matchThreshold) {
            if (end <= o.start || start >= o.end) {
                return false;
            }
            if (probability + o.probability < probThreshold) {
                return false;
            }

            int i = findFirst(word, ALPHA_NUMERIC_FILTER);
            int j = findFirst(o.word, ALPHA_NUMERIC_FILTER);
            int matching = 0;
            int notMatching = 0;

            while (0 <= i && i < word.length() && 0 <= j && j < o.word.length()) {
                if (Character.toLowerCase(word.charAt(i)) == Character.toLowerCase(o.word.charAt(j))) {
                    matching++;
                } else {
                    notMatching++;
                }
                i = findNext(word, ALPHA_NUMERIC_FILTER, i);
                j = findNext(o.word, ALPHA_NUMERIC_FILTER, j);
            }

            while (0 <= i && i < word.length()) {
                notMatching++;
                i = findNext(word, ALPHA_NUMERIC_FILTER, i);
            }

            while (0 <= j && j < o.word.length()) {
                notMatching++;
                j = findNext(o.word, ALPHA_NUMERIC_FILTER, j);
            }

            return (float) notMatching / (matching + notMatching) <= matchThreshold;
        }
    }

}
