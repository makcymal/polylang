package tech.makcymal.polylang.talks.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GigachatMessage {

    private String role;
    private String content;

    public static GigachatMessage fromUser(String content) {
        return new GigachatMessage("user", content);
    }

}
