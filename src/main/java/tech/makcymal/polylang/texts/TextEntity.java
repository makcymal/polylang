package tech.makcymal.polylang.texts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.langs.Lang;
import tech.makcymal.polylang.langs.LangLevel;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "texts")
public class TextEntity {

    @Id
    private UUID id;

    private String content;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "lang",
            write = "?::lang_t"
    )
    private Lang lang;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "level",
            write = "?::lang_level_t"
    )
    private LangLevel level;

    private String source;

}
