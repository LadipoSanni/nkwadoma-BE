package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

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
    private BigDecimal amountReceived;
    private List<LoanBreakdownResponse> loanBreakdown;

}
