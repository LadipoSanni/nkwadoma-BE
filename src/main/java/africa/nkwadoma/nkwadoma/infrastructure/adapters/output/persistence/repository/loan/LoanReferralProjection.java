package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;

import java.math.*;
import java.time.*;

public interface LoanReferralProjection {
    String getId();
    String getLoaneeId();
    String getFirstName();
    String getLastName();
    String getAlternateEmail();
    String getAlternatePhoneNumber();
    String getAlternateContactAddress();
    String getCohortName();
    String getLoaneeImage();
    String getReferredBy();
    BigDecimal getLoanAmountRequested();
    BigDecimal getInitialDeposit();
    BigDecimal getTuitionAmount();
    LocalDate getCohortStartDate();
    String getProgramName();
    boolean getIdentityVerified();
    LoanReferralStatus getStatus();
}
