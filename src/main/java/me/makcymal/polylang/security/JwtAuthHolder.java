package me.makcymal.polylang.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class JwtAuthHolder {

    public Authentication get() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        Authentication auth = Optional.of(ctx)
            .map(SecurityContext::getAuthentication)
            .orElse(null);
        return auth;
    }

    public void set(Authentication auth) {
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(auth);
    }

    public void forget() {
        var ctx = SecurityContextHolder.getContext();
        ctx.setAuthentication(null);
    }

}
