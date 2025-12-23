package tech.makcymal.polylang.security.dtos;

import lombok.Data;
import tech.makcymal.polylang.languages.StudiedLanguageDto;
import tech.makcymal.polylang.languages.Language;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
public class RegisterRequest {

    @Email
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Language nativeLanguage;
    private List<StudiedLanguageDto> studiedLanguages;

}
