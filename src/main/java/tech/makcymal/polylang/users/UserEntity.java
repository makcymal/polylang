package tech.makcymal.polylang.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.common.AbstractEntity;
import tech.makcymal.polylang.languages.Language;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class UserEntity {

    @Id
    private UUID id;

    private String email;

    private Boolean emailConfirmed;

    private String username;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "native_language",
            write = "?::language_t"
    )
    private Language nativeLanguage;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private ZonedDateTime lastAuthenticationAt;

    private ZonedDateTime lastPasswordResetAt;

}
