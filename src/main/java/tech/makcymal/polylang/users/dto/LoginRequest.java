package tech.makcymal.polylang.users.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank
    private String emailOrUsername;

    @NotBlank
    private String password;

}
