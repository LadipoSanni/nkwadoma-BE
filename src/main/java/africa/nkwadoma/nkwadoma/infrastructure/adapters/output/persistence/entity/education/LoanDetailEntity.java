package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;
    private BigDecimal totalOutstanding;
    private Double repaymentPercentage;
    private Double debtPercentage;
    private BigDecimal totalInterestIncurred;
    private BigDecimal monthlyExpected;
    private BigDecimal lastMonthActual;
}
