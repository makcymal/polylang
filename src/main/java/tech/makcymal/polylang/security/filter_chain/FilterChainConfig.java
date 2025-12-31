package tech.makcymal.polylang.security.filter_chain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfig {

    private String[] allowedUrls = new String[]{
        "/users/check-if-exists/*",
        "/users/register",
        "/users/confirm",
        "/users/login",
    };

    private final ExceptionalEntryPoint exceptionalEntryPoint;

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        return httpSecurity.cors(customizer -> {})
            .csrf(AbstractHttpConfigurer::disable)
            // .csrf(csrf -> csrf
            //     .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            //     .ignoringRequestMatchers("/api/auth/**") // Allow auth endpoints
            // )
            .exceptionHandling(c -> c
                .authenticationEntryPoint(exceptionalEntryPoint))
            // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(c -> c
                .requestMatchers(allowedUrls).permitAll()
                .anyRequest().authenticated())
            .sessionManagement(c -> c
                .sessionCreationPolicy(STATELESS))
            .build();
    }

}
