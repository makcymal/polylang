package tech.makcymal.polylang.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.makcymal.polylang.security.context.AlgorithmContext;
import tech.makcymal.polylang.security.context.JwtAuth;
import tech.makcymal.polylang.security.exceptions.BadTokenException;
import tech.makcymal.polylang.security.exceptions.ExpiredTokenException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String ACCESS_JWT_COOKIE = "access-jwt";

    private final AlgorithmContext algoCtx;

    public String getJwtValueFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(ACCESS_JWT_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public DecodedJWT decodeAndVerifyJwt(String value) {
        DecodedJWT jwt;
        try {
            jwt = JWT.decode(value);
            algoCtx.getAlgorithm().verify(jwt);
        } catch (JWTDecodeException e) {
            log.warn("JWT decode error: {}", value);
            throw new BadTokenException("Couldn't decode token", e);
        } catch (SignatureVerificationException e) {
            log.warn("JWT signature verification error: {}", value);
            throw new BadTokenException("Couldn't verify token signature", e);
        } catch (RuntimeException e) {
            log.warn("JWT verification error: {}", value);
            throw new BadTokenException("Couldn't verify token", e);
        }

        Instant now = Instant.now();
        Instant expiresAt = jwt.getExpiresAtAsInstant();
        if (expiresAt.isBefore(now)) {
            throw new ExpiredTokenException("Token is expired");
        }

        return jwt;
    }

    public JwtAuth getAuthFromDecodedJwt(DecodedJWT jwt) {
        JwtAuth auth = JwtAuth.builder()
                .authenticated(true)
                .claims(jwt.getClaims())
                .email(jwt.getSubject())
                .build();

        return auth;
    }

    public String issueJwt(UUID jti, String sub, Duration validityDuration) {
        ZonedDateTime now = ZonedDateTime.now();
        String jwt = JWT.create()
                .withJWTId(jti.toString())
                .withSubject(sub)
                .withExpiresAt(now.plus(validityDuration).toInstant())
                .sign(algoCtx.getAlgorithm());
        return jwt;
    }

}
