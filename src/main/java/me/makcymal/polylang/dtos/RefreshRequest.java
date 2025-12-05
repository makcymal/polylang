package me.makcymal.polylang.dtos;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class RefreshRequest {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$")
    private String refreshToken;

}
