package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import java.math.BigDecimal;

public interface LoanSummaryProjection {
    BigDecimal getTotalAmountReceived();
    BigDecimal getTotalAmountRepaid();
    BigDecimal getTotalAmountOutstanding();
    Integer getNumberOfLoanee();
}