package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CohortProjection {

    String getId();
    String getName();
    Integer getNumberOfLoanees();
    LocalDate getStartDate();
    BigDecimal getTuitionAmount();
    BigDecimal getAmountRequested();
    BigDecimal getAmountReceived();
    BigDecimal getAmountOutstanding();

}
