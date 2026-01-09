package tech.makcymal.polylang.texts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.langs.Lang;
import tech.makcymal.polylang.langs.LangLevel;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextDto {

    private UUID id;
    private String content;
    private Lang lang;
    private LangLevel level;
    private String source;

}
