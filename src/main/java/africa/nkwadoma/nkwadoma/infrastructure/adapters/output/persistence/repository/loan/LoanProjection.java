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
    String getLoaneeImage();
    String getReferredBy();
    LocalDateTime getCreatedDate();
    BigDecimal getTuitionAmount();
    String getGender();
    String getDateOfBirth();
    String getStateOfOrigin();
    String getMaritalStatus();
    String getStateOfResidence();
    String getNationality();
    String getResidentialAddress();
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
