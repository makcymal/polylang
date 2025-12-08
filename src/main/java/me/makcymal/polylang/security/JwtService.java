package me.makcymal.polylang.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.makcymal.polylang.exceptions.auth.BadTokenException;
import me.makcymal.polylang.exceptions.auth.ExpiredTokenException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_PREFIX = "Bearer ";

    private final AlgorithmContext algoCtx;
    private final SecurityProperties props;

    public String getJwtValueFromRequest(HttpServletRequest request) {
        String value = Optional.ofNullable(request.getHeader(AUTH_HEADER))
                .filter(h -> h.startsWith(AUTH_PREFIX))
                .map(h -> h.substring(AUTH_PREFIX.length()))
                .orElse(null);
        return value;
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

    public String issueAccess(String jti, String email) {
        ZonedDateTime now = ZonedDateTime.now();
        String jwt = JWT.create()
                .withJWTId(jti)
                .withSubject(email)
                .withExpiresAt(now.plusSeconds(props.getAccessTokenValiditySeconds()).toInstant())
                .sign(algoCtx.getAlgorithm());
        return jwt;
    }

    public String issueRefresh(String jti, String accessJti, String email) {
        ZonedDateTime now = ZonedDateTime.now();
        String jwt = JWT.create()
                .withJWTId(jti)
                .withClaim("access_jti", accessJti)
                .withSubject(email)
                .withExpiresAt(now.plusDays(props.getRefreshTokenValidityDays()).toInstant())
                .sign(algoCtx.getAlgorithm());
        return jwt;
    }

}
