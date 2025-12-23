package tech.makcymal.polylang.speaking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import tech.makcymal.polylang.common.AbstractEntity;
import tech.makcymal.polylang.texts.TextEntity;
import tech.makcymal.polylang.users.entities.UserEntity;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "speakings")
public class SpeakingEntity extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "text_id")
    private TextEntity text;

    private String transcription;

    private String analysis;

    private Integer score;

    @CreationTimestamp
    private ZonedDateTime createdAt;

}
