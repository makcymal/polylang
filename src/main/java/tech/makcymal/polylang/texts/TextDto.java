package tech.makcymal.polylang.texts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.languages.Language;
import tech.makcymal.polylang.languages.LanguageLevel;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextDto {

    private Integer id;

    private String content;

    private Language language;

    private LanguageLevel intendedLevel;

}
