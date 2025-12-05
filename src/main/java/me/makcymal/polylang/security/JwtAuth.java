package me.makcymal.polylang.security;

import com.auth0.jwt.interfaces.Claim;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class JwtAuth implements Authentication {

    private boolean authenticated;

    private Map<String, Claim> claims;

    private String email;

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return email;
    }

    @Override
    public String getName() {
        return email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
