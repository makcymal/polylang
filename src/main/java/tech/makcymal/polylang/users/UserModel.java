package tech.makcymal.polylang.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.langs.Lang;

import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserModel {

    private UUID id;

    private String email;

    private boolean emailConfirmed;

    private String username;

    private Lang nativeLang;

}
