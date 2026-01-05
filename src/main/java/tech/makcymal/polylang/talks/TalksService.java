package tech.makcymal.polylang.talks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalksService {

    private final TalksRepo repo;

    public UUID createNewTalk() {
        TalkEntity entity = TalkEntity.builder()
                .id(UUID.randomUUID())
                .build();
        TalkEntity savedEntity = repo.save(entity);
        return savedEntity.getId();
    }

}
