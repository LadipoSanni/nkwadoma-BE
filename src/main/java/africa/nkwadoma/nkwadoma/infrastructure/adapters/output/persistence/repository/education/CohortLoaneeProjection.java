package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import java.math.BigDecimal;

public interface CohortLoaneeProjection {

    String getId();
    String getFirstName();
    String getLastName();
    BigDecimal getAmountReceived();
    BigDecimal getAmountPaid();
    BigDecimal getAmountOutstanding();
    Double getInterestRate();
    Double getDebtPercentage();
    Double getRepaymentPercentage();
    String getGender();
    String getDateOfBirth();
    String getMaritalStatus();
    String getNationality();
    String getStateOfOrigin();
    String getStateOfResidence();
    String getResidentialAddress();
    String getPhoneNumber();
    String getAlternateEmail();
    String getAlternatePhoneNumber();
    String getAlternateContactAddress();
    String getAlternateResidenceAddress();
    String getNextOfKinPhoneNumber();
    String getNextOfKinFirstName();
    String getNextOfKinLastName();
    String getNextOfKinResidentialAddress();
    String getNextOfKinRelationship();
    String getHighestLevelOfEducation();
    String getProgramName();
    String getOrganizationName();
    String getCohortName();
    String getLoaneeLoanDetailId();
    String getInterestIncurred();
}
