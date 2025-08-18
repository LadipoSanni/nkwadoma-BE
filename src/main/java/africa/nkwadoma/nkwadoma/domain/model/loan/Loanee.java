package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.IdentityMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

import java.math.BigDecimal;
import java.time.*;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loanee {
    private String id;
    private String cohortId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime creditScoreUpdatedAt;
    private int creditScore;
    private String registryId;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;
    private List<LoaneeLoanBreakdown> loanBreakdowns;
    private LoaneeStatus loaneeStatus;
    private OnboardingMode onboardingMode;
    private UploadedStatus uploadedStatus;
    private LocalDateTime referralDateTime;
    private String referredBy;
    private String reasonForDropout;
    private List<RepaymentHistory> repaymentHistories;
    private String cohortName;
    private String loanProductName;
    private LocalDate cohortStartDate;
    private String programName;
    private String programId;
    private String loanId;
    private String deferReason;
    private LocalDateTime deferredDateAndTime;
    private ActivationStatus activationStatus;
    private String loaneeName;
    private LoanStatus loanStatus;
    private boolean deferralRequested;
    private boolean deferralApproved;
    private boolean dropoutRequested;
    private boolean dropoutApproved;
    private Double interestRate;
    private int paymentMoratoriumPeriod;
    private String termsAndConditions;
    private int tenor;
    private String institutionName;
    private String loanReferralId;
    private String cohortLoaneeId;
    private String organizationId;


    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateObjectInstance(loaneeLoanDetail, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateObjectInstance(loaneeLoanDetail, "Please provide loanee loan details.");
        if (loaneeLoanDetail.getInitialDeposit() == null) {
            loaneeLoanDetail.setInitialDeposit(BigDecimal.valueOf(0));
        }
        validateLoaneeUserIdentity();
    }

    public void validateLoaneeUserIdentity() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        MeedlValidator.validateDataElement(userIdentity.getFirstName(), "User first name is required.");
        MeedlValidator.validateDataElement(userIdentity.getLastName(), "User last name is required.");
        MeedlValidator.validateEmail(userIdentity.getEmail());
        MeedlValidator.validateUUID(userIdentity.getCreatedBy(), "Id of actor performing this action is required.");
    }

    public void validateForSaving() throws MeedlException {
        MeedlValidator.validateObjectInstance(userIdentity, IdentityMessages.USER_IDENTITY_CANNOT_BE_NULL.getMessage());
        validateLoaneeUserIdentity();
    }
}
