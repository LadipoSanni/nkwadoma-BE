package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
public class LoaneeLoanDetail {
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private List<LoanBreakdown> loanBreakdown = new ArrayList<>();
}
