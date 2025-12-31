package tech.makcymal.polylang.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmEmailResponse {

    private String email;
    private boolean confirmed;

}
