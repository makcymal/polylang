package tech.makcymal.polylang.languages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudiedLanguageDto {

    private Integer id;

    private Integer userId;

    private Language language;

    private LanguageLevel declaredLevel;

    private LanguageLevel estimatedLevel;

}
