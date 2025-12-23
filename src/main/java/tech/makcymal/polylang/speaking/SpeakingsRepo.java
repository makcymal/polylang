package tech.makcymal.polylang.speaking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeakingsRepo extends JpaRepository<SpeakingEntity, Integer> {
}
