package tech.makcymal.polylang.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tech.makcymal.polylang.langs.Lang;
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
            read = "native_lang",
            write = "?::lang_t"
    )
    private Lang nativeLang;

    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;

    private ZonedDateTime lastAuthenticatedAt;

    private ZonedDateTime lastResetPasswordAt;

}
