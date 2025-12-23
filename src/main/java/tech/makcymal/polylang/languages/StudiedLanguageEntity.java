package tech.makcymal.polylang.languages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.common.AbstractEntity;
import tech.makcymal.polylang.users.entities.UserEntity;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "studied_languages")
public class StudiedLanguageEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "language",
            write = "?::language_t"
    )
    private Language language;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "declared_level",
            write = "?::language_level_t"
    )
    private LanguageLevel declaredLevel;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "estimated_level",
            write = "?::language_level_t"
    )
    private LanguageLevel estimatedLevel;

}
