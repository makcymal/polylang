package tech.makcymal.polylang.users.repos;

import tech.makcymal.polylang.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepo extends JpaRepository<UserEntity, Integer> {
}
