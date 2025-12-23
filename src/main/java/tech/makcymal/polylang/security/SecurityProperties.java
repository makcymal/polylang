package tech.makcymal.polylang.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class SecurityProperties {

    @NotBlank
    private String privateKey;

    @NotBlank
    private String publicKey;

    @Min(0)
    @NotNull
    private Integer accessTokenValiditySeconds;

    @Min(0)
    @NotNull
    private Integer refreshTokenValidityDays;

}
