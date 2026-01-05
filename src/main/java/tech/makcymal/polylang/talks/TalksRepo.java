package tech.makcymal.polylang.talks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TalksRepo extends JpaRepository<TalkEntity, UUID> {
}
