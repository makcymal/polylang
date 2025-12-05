package me.makcymal.polylang.repositories;

import me.makcymal.polylang.entities.EmailConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailConfirmationCodesRepository extends JpaRepository<EmailConfirmationCode, Integer> {
}
