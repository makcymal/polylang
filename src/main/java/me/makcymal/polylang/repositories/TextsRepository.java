package me.makcymal.polylang.repositories;

import me.makcymal.polylang.entities.Text;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextsRepository extends JpaRepository<Text, Integer> {
}
