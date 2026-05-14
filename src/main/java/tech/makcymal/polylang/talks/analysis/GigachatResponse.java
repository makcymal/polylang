package tech.makcymal.polylang.talks.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

import static org.springframework.util.CollectionUtils.isEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GigachatResponse {

    private List<GigachatMessageWrapper> choices;

    public String getMessage() {
        if (isEmpty(choices)) {
            return null;
        }

        return choices.stream()
                .filter(Objects::nonNull)
                .filter(mw -> "assistant".equalsIgnoreCase(mw.getMessage().getRole()))
                .map(mw -> mw.getMessage().getContent())
                .filter(Objects::nonNull)
                .filter(m -> !m.isBlank())
                .findFirst()
                .orElse(null);
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GigachatMessageWrapper {
        private GigachatMessage message;
    }

}
