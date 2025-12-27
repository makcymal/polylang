package tech.makcymal.polylang.speaking.transcribe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionDiff {

    private String text;
    private boolean prevLastWordRevised;

}
