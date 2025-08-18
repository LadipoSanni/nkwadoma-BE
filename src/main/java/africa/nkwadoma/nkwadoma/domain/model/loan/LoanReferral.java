package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import lombok.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.*;
import java.time.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoanReferral {
    private String id;
    private String reasonForDeclining;
    private String firstName;
    private String lastName;
    private String loaneeUserId;
    private Loanee loanee;
    private CohortLoanee cohortLoanee;
    private LoanReferralStatus loanReferralStatus;
    private String referredBy;
    private boolean identityVerified;
    private String cohortName;
    private String loaneeImage;
    private BigDecimal loanAmountRequested;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    private LocalDate cohortStartDate;
    private String programName;
    private List<LoaneeLoanBreakdown> loaneeLoanBreakdowns;
    private String cohortLoaneeId;

    public void validateLoanReferralStatus() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferralStatus, LoanMessages.LOAN_REFERRAL_STATUS_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateLoanDecision(loanReferralStatus.name());
    }

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferralStatus, LoanMessages.LOAN_REFERRAL_STATUS_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternateContactAddress(), "Alternate Contact Address is required");
        MeedlValidator.validateEmail(loanee.getUserIdentity().getAlternateEmail());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternatePhoneNumber(), "Alternate Phone Number is required");
    }

    public void validateForCreate() throws MeedlException {
        MeedlValidator.validateObjectInstance(cohortLoanee, CohortMessages.COHORT_LOANEE_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateObjectInstance(cohortLoanee.getCohort(), CohortMessages.COHORT_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(cohortLoanee.getLoanee(), LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loanReferralStatus,"LoanReferral Status is required");
    }

}
