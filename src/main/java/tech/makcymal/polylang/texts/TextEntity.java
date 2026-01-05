package tech.makcymal.polylang.texts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.languages.Language;
import tech.makcymal.polylang.languages.LanguageLevel;
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
            read = "language",
            write = "?::language_t"
    )
    private Language language;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "declared_level",
            write = "?::language_level"
    )
    private LanguageLevel intendedLevel;

}
