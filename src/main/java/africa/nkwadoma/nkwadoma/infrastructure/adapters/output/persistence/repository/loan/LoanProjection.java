package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;

import java.math.*;
import java.time.*;

public interface LoanProjection {
    String getId();
    String getLoaneeId();
    String getCohortId();
    String getFirstName();
    String getEmail();
    String getPhoneNumber();
    LoanRequestStatus getStatus();
    String getLastName();
    String getCohortName();
    String getProgramName();
    BigDecimal getLoanAmountRequested();
    BigDecimal getLoanAmountApproved();
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
    String getCohortLoaneeId();
    String getOrganizationName();
    BigDecimal getAmountOutstanding();
    BigDecimal getAmountRepaid();
    Double getInterestRate();
    BigDecimal getInterestIncurred();
}
