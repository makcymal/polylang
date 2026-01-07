package tech.makcymal.polylang.users.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokensRepo extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByJtiAndExpiresAtAfter(UUID jti, ZonedDateTime expiresAtAfter);

    @Transactional
    void deleteByJti(UUID jti);

    @Transactional
    void deleteAllByUserId(UUID userId);

}
