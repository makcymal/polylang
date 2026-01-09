package tech.makcymal.polylang.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.langs.Lang;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatDto {

    private Integer userId;

    private Lang lang;

    private HistoryEntry history;

}
