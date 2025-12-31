package tech.makcymal.polylang.users.tokens;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    private UUID jti;

    private UUID accessJti;

    private UUID userId;

    private ZonedDateTime expiresAt;

}
