package tech.makcymal.polylang.talks.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static tech.makcymal.polylang.common.CommonUtils.mutableListOf;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GigachatRequest {

   private String model;
   private List<GigachatMessage> messages;
   private float temperature;
   private boolean stream;

   public static GigachatRequest withPrompt(String message) {
       return GigachatRequest.builder()
               .model("GigaChat-Max")
               .messages(mutableListOf(GigachatMessage.fromUser(message)))
               .temperature(1.0f)
               .stream(false)
               .build();
   }

}
