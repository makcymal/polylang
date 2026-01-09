package tech.makcymal.polylang.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tech.makcymal.polylang.langs.Lang;
import tech.makcymal.polylang.users.UserEntity;
import org.hibernate.annotations.ColumnTransformer;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "stats")
public class StatEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "lang",
            write = "?::lang_t"
    )
    private Lang lang;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<HistoryEntry> history;

}
