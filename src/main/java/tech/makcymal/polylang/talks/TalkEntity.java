package tech.makcymal.polylang.talks;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "talks")
public class TalkEntity {

    @Id
    private UUID id;

    private UUID userId;

    private UUID textId;

    private String transcription;

    private String analysis;

    private Integer score;

    @CreationTimestamp
    private ZonedDateTime createdAt;

}
