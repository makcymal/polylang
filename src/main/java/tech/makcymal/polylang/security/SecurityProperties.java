package tech.makcymal.polylang.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    @NotBlank
    private String privateKey;

    @NotBlank
    private String publicKey;

    @NotNull
    private Duration accessTokenValidity;

    @NotNull
    private Duration refreshTokenValidity;

    @NotNull
    private Duration emailConfirmationCodeValidity;

    private boolean cookiesAttrSecure;

    @NotNull
    private String cookiesAttrSameSite;

}
