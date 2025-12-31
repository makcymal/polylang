package tech.makcymal.polylang.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.users.UserModel;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Cookies {

    private UserModel currentUser;
    private String accessJwt;
    private UUID refreshJti;

    public static Cookies ofNulls() {
        return new Cookies(null, null, null);
    }

}
