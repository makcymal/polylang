package tech.makcymal.polylang.security.filter_chain;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatchers;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.makcymal.polylang.security.JwtService;
import tech.makcymal.polylang.security.context.JwtAuth;
import tech.makcymal.polylang.security.context.JwtAuthHolder;
import tech.makcymal.polylang.security.exceptions.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final RequestMatcher permitMatcher = RequestMatchers.anyOf(
            PathPatternRequestMatcher.pathPattern("/users/**"),
            PathPatternRequestMatcher.pathPattern("/texts/random")
    );

    private final JwtAuthHolder authHolder;
    private final ExceptionalEntryPoint exceptionalEntryPoint;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        JwtAuth auth = authHolder.get();

        try {
            String value = jwtService.getJwtValueFromRequest(request);
            if (value == null) {
                throw new AuthException(HttpStatus.UNAUTHORIZED, "no jwt provided");
            }

            DecodedJWT jwt = jwtService.decodeAndVerifyJwt(value);
            auth.setAuthenticated(true);
            auth.setUserId(UUID.fromString(jwt.getSubject()));

        } catch (RuntimeException e) {
            if (
                    e instanceof AuthException authException
                    && HttpStatus.UNAUTHORIZED.equals(authException.getHttpStatus())
                    && permitMatcher.matches(request)
            ) {
                auth.setAuthenticated(true);

            } else {
                AuthenticationException error = new AuthException(HttpStatus.UNAUTHORIZED, e);
                exceptionalEntryPoint.commence(request, response, error);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

}
