package africa.nkwadoma.nkwadoma.domain.model.loan;

import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import lombok.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.*;
import java.time.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoanReferral {
    private String id;
    private String loaneeUserId;
    private Loanee loanee;
    private LoanReferralStatus loanReferralStatus;
    private String referredBy;
    private String cohortName;
    private String loaneeImage;
    private BigDecimal loanAmountRequested;
    private BigDecimal initialDeposit;
    private BigDecimal tuitionAmount;
    private LocalDate cohortStartDate;
    private String programName;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferralStatus);
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternateContactAddress());
        MeedlValidator.validateEmail(loanee.getUserIdentity().getAlternateEmail());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternatePhoneNumber());
    }

    public void validateViewLoanReferral() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanee.getUserIdentity());
        MeedlValidator.validateUUID(loanee.getUserIdentity().getId(), UserMessages.INVALID_USER_ID.getMessage());
    }

    public void validateForCreate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanReferralStatus);
    }

}
