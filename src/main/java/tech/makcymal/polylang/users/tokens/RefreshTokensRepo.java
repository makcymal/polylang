package tech.makcymal.polylang.users.tokens;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokensRepo extends JpaRepository<RefreshTokenEntity, UUID> {

    void deleteAllByUserId(int userId);

    void deleteByJti(UUID jti);

    Optional<RefreshTokenEntity> findByJti(UUID jti);

}
