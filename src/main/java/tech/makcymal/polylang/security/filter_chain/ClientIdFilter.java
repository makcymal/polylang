package tech.makcymal.polylang.security.filter_chain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tech.makcymal.polylang.security.SecurityProperties;
import tech.makcymal.polylang.security.context.JwtAuth;
import tech.makcymal.polylang.security.context.JwtAuthHolder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

import static tech.makcymal.polylang.common.CommonUtils.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientIdFilter extends OncePerRequestFilter {

    private static final String CLIENT_ID_COOKIE = "client-id";
    private static final String SAME_SITE_COOKIE_ATTR = "SameSite";

    private final SecurityProperties props;
    private final JwtAuthHolder authHolder;

    public UUID getClientIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CLIENT_ID_COOKIE)) {
                    return UUID.fromString(cookie.getValue());
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        UUID clientId = getClientIdFromRequest(request);

        if (clientId == null) {
            sleep(Duration.ofSeconds(3));

            clientId = UUID.randomUUID();
            Cookie cookie = new Cookie(CLIENT_ID_COOKIE, clientId.toString());
            cookie.setHttpOnly(true);
            cookie.setSecure(props.isCookiesAttrSecure());
            cookie.setAttribute(SAME_SITE_COOKIE_ATTR, props.getCookiesAttrSameSite());
            cookie.setPath("/");
            cookie.setMaxAge((int) Duration.ofDays(365).toSeconds());
            response.addCookie(cookie);
        }

        JwtAuth auth = JwtAuth.builder()
                .authenticated(false)
                .userId(null)
                .clientId(clientId)
                .build();
        authHolder.set(auth);

        filterChain.doFilter(request, response);
    }

}
