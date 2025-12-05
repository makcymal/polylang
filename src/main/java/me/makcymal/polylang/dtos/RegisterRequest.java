package me.makcymal.polylang.dtos;

import lombok.Data;
import me.makcymal.polylang.enums.Language;

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
