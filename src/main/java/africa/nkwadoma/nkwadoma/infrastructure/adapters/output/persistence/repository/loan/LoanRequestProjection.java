package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import java.math.*;
import java.time.*;

public interface LoanRequestProjection {
    String getId();
    String getLoaneeId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getCohortName();
    String getLoaneeImage();
    String getReferredBy();
    BigDecimal getLoanAmountRequested();
    LocalDateTime getCreatedDate();
    BigDecimal getInitialDeposit();
    BigDecimal getTuitionAmount();
    LocalDate getCohortStartDate();
    String getGender();
    String getDateOfBirth();
    String getStateOfOrigin();
    String getMaritalStatus();
    String getStateOfResidence();
    String getNationality();
    String getResidentialAddress();
    String getProgramName();
    String getAlternatePhoneNumber();
    String getAlternateEmail();
    String getAlternateContactAddress();
    String getNextOfKinId();
    String getNextOfKinFirstName();
    String getNextOfKinLastName();
    String getNextOfKinEmail();
    String getNextOfKinContactAddress();
    String getNextOfKinRelationship();
    String getNextOfKinPhoneNumber();
}
