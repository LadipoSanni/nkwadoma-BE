package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.LoanBreakdownResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class LoaneeLoanDetailResponse {
    private String id;
    private BigDecimal tuitionAmount;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private BigDecimal amountReceived;
    private BigDecimal amountRepaid;
    private BigDecimal amountOutstanding;
    private List<LoanBreakdownResponse> loanBreakdown;

}
