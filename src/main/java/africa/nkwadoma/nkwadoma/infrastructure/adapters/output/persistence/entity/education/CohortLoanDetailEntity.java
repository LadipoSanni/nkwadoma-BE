package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
@Entity
public class CohortLoanDetailEntity {
    @Id
    @UuidGenerator
    private String id;
    @OneToOne(fetch = FetchType.EAGER)
    private CohortEntity cohort;
    private BigDecimal totalAmountRequested = BigDecimal.ZERO;
    private BigDecimal totalOutstandingAmount = BigDecimal.ZERO;
    private BigDecimal totalAmountReceived  = BigDecimal.ZERO;
    private BigDecimal totalAmountRepaid  = BigDecimal.ZERO;
}
