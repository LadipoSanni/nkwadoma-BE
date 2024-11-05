package africa.nkwadoma.nkwadoma.domain.model.education;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoanBreakdown {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String loanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount = BigDecimal.ZERO;
    private String currency;
    @ManyToOne
    private Cohort cohort;
}
