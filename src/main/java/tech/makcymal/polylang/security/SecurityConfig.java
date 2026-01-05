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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import tech.makcymal.polylang.security.filter_chain.ExceptionalEntryPoint;
import tech.makcymal.polylang.security.filter_chain.JwtAuthFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final String[] allowedUrls = new String[]{
        // "/users/**"
        "/**"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final ExceptionalEntryPoint exceptionalEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        return httpSecurity.cors(customizer -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(c -> c
                    .sessionCreationPolicy(STATELESS))
            .addFilterBefore(jwtAuthFilter, BasicAuthenticationFilter.class)
            .authorizeHttpRequests(c -> c
                    .requestMatchers(allowedUrls).permitAll()
                    .anyRequest().authenticated())
            .exceptionHandling(c -> c
                    .authenticationEntryPoint(exceptionalEntryPoint))
            .build();
    }

    @Bean
    public AuthenticationManager noopAuthenticationManager() {
        return authentication -> {
            throw new AuthenticationServiceException("Authentication is disabled");
        };
    }

}
