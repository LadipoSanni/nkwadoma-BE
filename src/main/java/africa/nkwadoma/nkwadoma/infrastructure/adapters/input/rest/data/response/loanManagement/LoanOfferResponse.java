package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loanManagement.loanee.LoaneeLoanBreakDownResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class LoanOfferResponse {

    private String id;
    private LocalDate startDate;
    private LoanOfferStatus loanOfferStatus;
    private BigDecimal tuitionAmount;
    private BigDecimal initialDeposit;
    private BigDecimal amountRequested;
    private List<LoaneeLoanBreakDownResponse> loaneeBreakdown;
    private LocalDateTime dateTimeOffered;
    private LoanDecision loaneeResponse;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String stateOfOrigin;
    private String maritalStatus;
    private String stateOfResidence;
    private String nationality;
    private String residentialAddress;
    private String alternateEmail;
    private String alternatePhoneNumber;
    private String alternateContactAddress;
    private String nextOfKinEmail;
    private String nextOfKinPhoneNumber;
    private String nextOfKinRelationship;
    private String nextOfKinFirstName;
    private String nextOfKinLastName;
    private String nextOfKinContactAddress;
    private String image;
    private String levelOfEducation;
    private String loanProductName;
    private String cohortName;
    private String programName;
    private int creditScore;
    private String termsAndCondition;
    private String loaneeId;
}
