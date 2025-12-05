package me.makcymal.polylang.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "email_confirmation_codes")
public class EmailConfirmationCode extends AbstractEntity {

    private String email;

    private String code;

    private ZonedDateTime expiresAt;

}
