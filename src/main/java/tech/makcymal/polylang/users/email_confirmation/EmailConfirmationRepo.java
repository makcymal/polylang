package tech.makcymal.polylang.users.email_confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailConfirmationRepo extends JpaRepository<EmailConfirmationEntity, UUID> {

    Optional<EmailConfirmationEntity> findByEmail(String email);

    @Transactional
    void deleteAllByEmailAndExpiresAtBefore(String email, ZonedDateTime expiresAtBefore);

}
