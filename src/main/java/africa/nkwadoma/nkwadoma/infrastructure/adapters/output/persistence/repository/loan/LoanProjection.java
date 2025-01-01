package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import java.math.*;
import java.time.*;

public interface LoanProjection {
    String getId();
    String getFirstName();
    String getLastName();
    String getCohortName();
    String getProgramName();
    BigDecimal getLoanAmountRequested();
    LocalDateTime getOfferDate();
    LocalDateTime getStartDate();
    BigDecimal getInitialDeposit();
    LocalDate getCohortStartDate();
}
