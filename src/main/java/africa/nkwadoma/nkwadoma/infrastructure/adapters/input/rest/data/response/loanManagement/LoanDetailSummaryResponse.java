package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LoanDetailSummaryResponse {

    private int numberOfLoanee;
    private BigDecimal totalAmountReceived;
    private BigDecimal totalAmountRepaid;
    private BigDecimal totalAmountOutstanding;
}
