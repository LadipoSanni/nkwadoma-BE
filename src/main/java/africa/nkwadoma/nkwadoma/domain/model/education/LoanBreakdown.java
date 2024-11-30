package africa.nkwadoma.nkwadoma.domain.model.education;

import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LoanBreakdown {
    private String loanBreakdownId;
    private String itemName;
    private BigDecimal itemAmount;
    private String currency;
    private Cohort cohort;
}
