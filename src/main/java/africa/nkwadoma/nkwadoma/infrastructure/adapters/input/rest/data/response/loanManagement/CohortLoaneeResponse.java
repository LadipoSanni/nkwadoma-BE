package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;


import africa.nkwadoma.nkwadoma.domain.enums.EmploymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class CohortLoaneeResponse {


    private String loanId;
    private String cohortId;
    private String loaneeId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    List<LoanBreakdownResponse> loanBreakdownResponses;
    private BigDecimal amountReceived;
    private BigDecimal amountRepaid;
    private BigDecimal amountOutstanding;
    private EmploymentStatus employmentStatus;
    private double interestRate;
    private double debtPercentage;
    private double repaymentPercentage;
    private String gender;
    private String dateOfBirth;
    private String maritalStatus;
    private String nationality;
    private String stateOfOrigin;
    private String stateOfResidence;
    private String residentialAddress;
    private String phoneNumber;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String alternateResidenceAddress;
    private String nextOfKinPhoneNumber;
    private String nextOfKinFirstName;
    private String nextOfKinLastName;
    private String nextOfKinResidentialAddress;
    private String nextOfKinRelationship;
    private String highestLevelOfEducation;
    private String trainingPerformance;
    private String programName;
    private String cohortName;
    private String organizationName;
    private String loanTermsAndCondition;
    private String interestIncurred;
}
