package tech.makcymal.polylang.users.email_confirmation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfirmationRepo extends JpaRepository<EmailConfirmationEntity, Integer> {

    void deleteAllByEmail(String email);

}
