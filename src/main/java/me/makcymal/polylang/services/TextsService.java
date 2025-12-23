package me.makcymal.polylang.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.makcymal.polylang.entities.Text;
import me.makcymal.polylang.repositories.TextsRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextsService {

    private final TextsRepository repo;

    public Text getRandomText() {
        return repo.getRandom();
    }

}
