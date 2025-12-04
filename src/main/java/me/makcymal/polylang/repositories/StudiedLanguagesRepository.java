package me.makcymal.polylang.repositories;

import me.makcymal.polylang.entities.StudiedLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudiedLanguagesRepository extends JpaRepository<StudiedLanguage, Integer> {
}
