package tech.makcymal.polylang.users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.languages.Language;
import tech.makcymal.polylang.languages.StudiedLanguageDto;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private Integer id;

    private String email;

    private Boolean emailConfirmed;

    private String username;

    private Language nativeLanguage;

    private List<StudiedLanguageDto> studiedLanguages;

}
