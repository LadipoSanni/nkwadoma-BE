package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.identity.NextOfKinResponse;
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
    private List<LoaneeLoanBreakdown> loaneeBreakdown;
    private LocalDateTime dateTimeOffered;
    private LoanDecision loaneeResponse;
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
}
