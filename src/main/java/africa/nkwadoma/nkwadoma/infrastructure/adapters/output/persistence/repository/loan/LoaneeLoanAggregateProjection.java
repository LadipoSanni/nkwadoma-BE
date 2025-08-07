package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import java.math.BigDecimal;

public interface LoaneeLoanAggregateProjection {

    String getLoaneeId();
    String getFirstName();
    String getLastName();
    String getEmail();
    BigDecimal getHistoricalDebt();
    BigDecimal getTotalAmountOutStanding();
    Integer getNumberOfLoans();
}
