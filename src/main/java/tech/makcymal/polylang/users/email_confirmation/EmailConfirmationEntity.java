package tech.makcymal.polylang.users.email_confirmation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "email_confirmation_codes")
public class EmailConfirmationEntity {

    @Id
    private UUID id;

    private String email;

    private String code;

    private ZonedDateTime expiresAt;

}
