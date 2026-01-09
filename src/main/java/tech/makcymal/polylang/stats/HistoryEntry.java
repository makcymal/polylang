package tech.makcymal.polylang.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.langs.LangLevel;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntry {

    private LangLevel level;
    private int talks;
    private float score;
    private ZonedDateTime reachedAt;

}
