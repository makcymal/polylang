package tech.makcymal.polylang.languages;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudiedLanguagesRepo extends JpaRepository<StudiedLanguageEntity, Integer> {
}
