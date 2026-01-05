package tech.makcymal.polylang.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import tech.makcymal.polylang.languages.Language;
import tech.makcymal.polylang.languages.StudiedLanguageDto;

import java.util.List;
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

    private Language nativeLanguage;

    private List<StudiedLanguageDto> studiedLanguages;

}
