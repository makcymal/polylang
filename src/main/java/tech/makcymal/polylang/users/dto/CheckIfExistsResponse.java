package tech.makcymal.polylang.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CheckIfExistsResponse {

    private boolean exists;
    private boolean isEmail;

}
