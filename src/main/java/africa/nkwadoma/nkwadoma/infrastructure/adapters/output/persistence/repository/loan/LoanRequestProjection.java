package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.education.*;

import java.math.*;
import java.time.*;
import java.util.*;

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
    BigDecimal getTuitionAmount();
    LocalDate getCohortStartDate();
    String getProgramName();
    String getAlternatePhoneNumber();
    String getAlternateEmail();
    String getAlternateContactAddress();
    String getNextOfKinFirstName();
    String getNextOfKinLastName();
    String getNextOfKinEmail();
    String getNextOfKinContactAddress();
    String getNextOfKinRelationship();
    String getNextOfKinPhoneNumber();
}
