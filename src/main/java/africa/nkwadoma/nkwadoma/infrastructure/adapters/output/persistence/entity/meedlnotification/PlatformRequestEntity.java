package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.meedlnotification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PlatformRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal obligorLoanLimit;
    private String createdBy;
    private LocalDateTime requestTime;
}
