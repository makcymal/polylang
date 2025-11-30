package me.makcymal.polylang.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.makcymal.polylang.enums.Language;
import me.makcymal.polylang.enums.LanguageLevel;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "texts")
public class Text extends AbstractEntity {

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
