package tech.makcymal.polylang.texts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextsService {

    private final TextsRepo repo;

    public TextEntity getRandomText() {
        return repo.getRandom();
    }

}
