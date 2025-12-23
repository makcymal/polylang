package tech.makcymal.polylang.texts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TextsRepo extends JpaRepository<TextEntity, Integer> {

    @Query(value =
            """
            SELECT *
            FROM texts
            WHERE id = (SELECT MAX(id)
                        FROM texts
                        WHERE id <= (SELECT MIN(id) + ROUND(RANDOM() * (MAX(id) - MIN(id)))
                                     FROM TEXTS))
            """,
           nativeQuery = true
    )
    TextEntity getRandom();

}
