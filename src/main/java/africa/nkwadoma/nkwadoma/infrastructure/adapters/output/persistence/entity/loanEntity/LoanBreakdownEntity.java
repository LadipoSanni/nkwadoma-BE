package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@ToString
public class LoanBreakdownEntity {
    @Id
    @UuidGenerator
    private String loanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount = BigDecimal.ZERO;
    private String currency;
    @ManyToOne
    private CohortEntity cohort;
    @ManyToOne
    private LoaneeEntity loanee;
}
