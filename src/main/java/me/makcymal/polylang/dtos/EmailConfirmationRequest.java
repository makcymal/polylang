package me.makcymal.polylang.dtos;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Data
public class EmailConfirmationRequest {

    @Email
    private String email;

    @Pattern(regexp = "^\\d{6}$")
    private String code;

}
