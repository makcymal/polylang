package tech.makcymal.polylang.security.filter_chain;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.makcymal.polylang.security.JwtService;
import tech.makcymal.polylang.security.context.JwtAuth;
import tech.makcymal.polylang.security.context.JwtAuthHolder;
import tech.makcymal.polylang.security.exceptions.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthHolder authHolder;
    private final ExceptionalEntryPoint exceptionalEntryPoint;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String value = jwtService.getJwtValueFromRequest(request);
            if (value != null) {
                DecodedJWT jwt = jwtService.decodeAndVerifyJwt(value);
                Authentication jwtAuth = jwtService.getAuthFromDecodedJwt(jwt);
                authHolder.set(jwtAuth);
            }
        } catch (RuntimeException e) {
            AuthenticationException error = new AuthException(HttpStatus.UNAUTHORIZED, e);
            exceptionalEntryPoint.commence(request, response, error);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            authHolder.forget();
        }
    }

}
