package me.makcymal.polylang.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.makcymal.polylang.enums.Language;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "users")
public class User extends AbstractEntity {

    private String email;

    private Boolean emailConfirmed;

    private String username;

    private UUID passwordSalt;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "native_language",
            write = "?::language_t"
    )
    private Language nativeLanguage;



}
