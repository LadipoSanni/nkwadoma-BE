package africa.nkwadoma.nkwadoma.domain.model.loan;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class LoanDetailSummary {
    private BigDecimal totalAmountReceived;
    private BigDecimal totalAmountRepaid;
    private BigDecimal totalAmountOutstanding;
    private int numberOfLoanee;
}
