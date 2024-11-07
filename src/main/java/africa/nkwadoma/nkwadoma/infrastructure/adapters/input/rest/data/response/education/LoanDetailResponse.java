package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.education;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class LoanDetailResponse {

    private String id;
    private BigDecimal totalAmountDisbursed;
    private BigDecimal totalAmountRepaid;
    private BigDecimal totalOutstanding;
    private Double repaymentPercentage;
    private Double debtPercentage;
    private BigDecimal totalInterestIncurred;
    private BigDecimal monthlyExpected;
    private BigDecimal lastMonthActual;
}
