package tech.makcymal.polylang.texts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static tech.makcymal.polylang.common.CommonUtils.mapNullable;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextsService {

    private final TextsRepo repo;
    private final TextsMapper mapper;

    public TextDto getRandomText() {
        return mapper.toDto(repo.findRandom());
    }

    public String getTextContentByTalkId(UUID talkId) {
        return mapNullable(repo.findByTalkId(talkId), TextEntity::getContent);
    }

}
