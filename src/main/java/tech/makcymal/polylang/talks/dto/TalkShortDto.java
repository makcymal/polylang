package tech.makcymal.polylang.talks.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.langs.Lang;
import tech.makcymal.polylang.langs.LangLevel;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkShortDto {

    private UUID id;
    private String textContentStart;
    private LangLevel textLevel;
    private Float score;
    private ZonedDateTime dt;

}
