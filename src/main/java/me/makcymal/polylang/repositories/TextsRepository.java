package me.makcymal.polylang.repositories;

import me.makcymal.polylang.entities.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TextsRepository extends JpaRepository<Text, Integer> {

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
    Text getRandom();

}
