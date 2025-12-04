package me.makcymal.polylang.repositories;

import me.makcymal.polylang.entities.Speaking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpekingsRepository extends JpaRepository<Speaking, Integer> {
}
