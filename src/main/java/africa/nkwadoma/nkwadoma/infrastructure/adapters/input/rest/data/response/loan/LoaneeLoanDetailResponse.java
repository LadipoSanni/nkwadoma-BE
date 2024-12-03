package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class LoaneeLoanDetailResponse {

    private BigDecimal tuitionAmount;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private List<LoanBreakdownResponse> loanBreakdown;

}
