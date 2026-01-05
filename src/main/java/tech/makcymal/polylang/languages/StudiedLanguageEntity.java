package tech.makcymal.polylang.languages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.users.UserEntity;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "studied_languages")
public class StudiedLanguageEntity {

    @Id
    private UUID id;

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
