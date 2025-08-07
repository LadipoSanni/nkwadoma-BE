package africa.nkwadoma.nkwadoma.domain.model.loan;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class LoaneeLoanAggregate {

    private String id;
    private BigDecimal historicalDebt;
    private BigDecimal totalAmountOutstanding;
    private int numberOfLoans;
    private Loanee loanee;
}
