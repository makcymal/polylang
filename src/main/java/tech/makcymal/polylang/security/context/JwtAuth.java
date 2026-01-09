package tech.makcymal.polylang.security.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class JwtAuth implements Authentication {

    private boolean authenticated;

    private UUID userId;

    private UUID clientId;

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return userId;
    }

    @Override
    public String getName() {
        return null;
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
