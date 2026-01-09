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
public class TalkFullDto {

    private UUID id;
    private UUID userId;
    private String textContent;
    private String textSource;
    private Lang textLang;
    private LangLevel textLevel;
    private String transcription;
    private String analysis;
    private Float score;
    private ZonedDateTime dt;

}
