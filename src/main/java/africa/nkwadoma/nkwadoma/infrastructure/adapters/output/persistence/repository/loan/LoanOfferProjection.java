package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import lombok.Getter;
import org.apache.james.mime4j.dom.datetime.DateTime;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface LoanOfferProjection {

    String getId();
    String getLoaneeId();
    String getLoanProductName();
    String getLoanProductId();
    LocalDate getStartDate();
    LoanOfferStatus getLoanOfferStatus();
    BigDecimal getTuitionAmount();
    BigDecimal getInitialDeposit();
    BigDecimal getAmountRequested();
    BigDecimal getAmountApproved();
    Integer getCreditScore();
    String getCohortName();
    String getLoanRequestId();
    String getTermsAndCondition();
    List<LoaneeLoanBreakdown> getLoaneeBreakdowns();
    LocalDateTime getDateTimeOffered();
    LoanDecision getLoaneeResponse();
    String getLoaneeImage();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPhoneNumber();
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
    String getOrganizationId();
    String getLoanRequestReferredBy();

}
