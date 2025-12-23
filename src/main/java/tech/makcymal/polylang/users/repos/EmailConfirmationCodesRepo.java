package tech.makcymal.polylang.users.repos;

import tech.makcymal.polylang.users.entities.EmailConfirmationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfirmationCodesRepo extends JpaRepository<EmailConfirmationCodeEntity, Integer> {
}
