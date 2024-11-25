package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import java.math.*;
import java.time.*;

public interface LoanRequestProjection {
    String getId();
    String getLoaneeId();
    String getFirstName();
    String getLastName();
    String getCohortName();
    String getReferredBy();
    BigDecimal getLoanAmountRequested();
    LocalDateTime getCreatedDate();
    BigDecimal getInitialDeposit();
    LocalDate getCohortStartDate();
    String getProgramName();
    String getAlternatePhoneNumber();
    String getAlternateEmail();
    String getAlternateContactAddress();
}
