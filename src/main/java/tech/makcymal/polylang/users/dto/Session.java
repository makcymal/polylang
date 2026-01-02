package tech.makcymal.polylang.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.users.UserModel;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class Session {

    @NotNull
    private UserModel currentUser;
    private UUID emailConfirmationCodeId;
    private String accessJwt;
    private UUID refreshJti;

    public static Session ofNulls() {
        return new Session(null, null,null, null);
    }

}
