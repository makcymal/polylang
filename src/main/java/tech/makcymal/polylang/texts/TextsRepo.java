package tech.makcymal.polylang.texts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TextsRepo extends JpaRepository<TextEntity, UUID> {

    @Query(value =
            """
            SELECT *
            FROM texts
            ORDER BY RANDOM()
            LIMIT 1
            """,
           nativeQuery = true
    )
    TextEntity findRandom();

    @Query(value =
            """
            SELECT *
            FROM texts
            WHERE id = (SELECT text_id FROM talks WHERE id = :talkId)
            """,
           nativeQuery = true
    )
    TextEntity findByTalkId(UUID talkId);

}
