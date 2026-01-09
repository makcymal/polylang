package tech.makcymal.polylang.users.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterRequest {

    @Email
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // private Lang nativeLang;

}
