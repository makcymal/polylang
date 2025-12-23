package tech.makcymal.polylang.users.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.common.AbstractEntity;

import jakarta.persistence.Entity;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "email_confirmation_codes")
public class EmailConfirmationCodeEntity extends AbstractEntity {

    private String email;

    private String code;

    private ZonedDateTime expiresAt;

}
