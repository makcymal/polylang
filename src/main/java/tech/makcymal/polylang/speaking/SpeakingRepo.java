package tech.makcymal.polylang.speaking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeakingRepo extends JpaRepository<SpeakingEntity, Integer> {
}
