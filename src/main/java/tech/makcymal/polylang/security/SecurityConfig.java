package tech.makcymal.polylang.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tech.makcymal.polylang.security.filter_chain.AntiFraudFilter;
import tech.makcymal.polylang.security.filter_chain.ClientIdFilter;
import tech.makcymal.polylang.security.filter_chain.ExceptionalEntryPoint;
import tech.makcymal.polylang.security.filter_chain.JwtFilter;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final String[] permittedUris = new String[]{
            "/error"
    };

    private final ClientIdFilter clientIdFilter;
    private final AntiFraudFilter antiFraudFilter;
    private final JwtFilter jwtFilter;
    private final ExceptionalEntryPoint exceptionalEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        return httpSecurity.cors(customizer -> {
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(clientIdFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(antiFraudFilter, ClientIdFilter.class)
                .addFilterAfter(jwtFilter, AntiFraudFilter.class)
                .authorizeHttpRequests(c -> c
                        .requestMatchers(permittedUris).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(exceptionalEntryPoint))
                .build();
    }

    @Bean
    public AuthenticationManager noopAuthenticationManager() {
        return _ -> {
            throw new AuthenticationServiceException("Authentication is disabled");
        };
    }

}
