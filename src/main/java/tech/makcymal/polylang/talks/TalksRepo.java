package tech.makcymal.polylang.talks;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface TalksRepo extends JpaRepository<TalkEntity, UUID> {

    @Query(value = """
                   UPDATE talks
                   SET transcription = :transcription
                   WHERE id = :id;
                   """,
           nativeQuery = true)
    @Modifying
    @Transactional
    void setTranscription(UUID id, String transcription);

    @Query(value = """
                   DELETE
                   FROM talks
                   WHERE created_at > NOW() + INTERVAL '1 day' AND (transcription IS NULL OR TRIM(transcription) = '');
                   """,
           nativeQuery = true)
    @Modifying
    @Transactional
    void deleteOldWithEmptyTranscription();

}
