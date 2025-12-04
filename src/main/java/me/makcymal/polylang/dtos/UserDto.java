package me.makcymal.polylang.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import me.makcymal.polylang.enums.Language;

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
