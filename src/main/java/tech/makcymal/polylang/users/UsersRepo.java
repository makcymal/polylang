package tech.makcymal.polylang.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepo extends JpaRepository<UserEntity, UUID> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    @Query(value = """
                   UPDATE users
                   SET email_confirmed = TRUE
                   WHERE email IN (SELECT email
                                   FROM email_confirmation_codes
                                   WHERE email = :email
                                     AND code = :code
                                     AND expires_at > NOW());
                   """,
           nativeQuery = true)
    @Modifying
    @Transactional
    void tryToConfirmEmail(String email, String code);

    @Query(value = """
                   UPDATE users
                   SET last_authenticated_at = :dt
                   WHERE id = :id
                   """,
           nativeQuery = true)
    @Modifying
    @Transactional
    void updateLastAuthenticationAt(UUID id, ZonedDateTime dt);

}
