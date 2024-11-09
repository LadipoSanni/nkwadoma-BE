package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class LoaneeLoanDetail {

    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private List<LoanBreakdown> loanBreakdown = new ArrayList<>();
}
