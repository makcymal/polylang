package tech.makcymal.polylang.languages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudiedLanguageDto {

    private UUID id;

    private Integer userId;

    private Language language;

    private LanguageLevel declaredLevel;

    private LanguageLevel estimatedLevel;

}
