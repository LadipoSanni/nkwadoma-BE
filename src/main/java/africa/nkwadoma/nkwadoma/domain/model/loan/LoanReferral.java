package africa.nkwadoma.nkwadoma.domain.model.loan;

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
    private Loanee loanee;
    private LoanReferralStatus loanReferralStatus;
    private String referredBy;
    String cohortName;
    String loaneeImage;
    BigDecimal loanAmountRequested;
    BigDecimal initialDeposit;
    BigDecimal tuitionAmount;
    LocalDate cohortStartDate;
    String programName;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanReferralStatus);
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternateContactAddress());
        MeedlValidator.validateEmail(loanee.getUserIdentity().getAlternateEmail());
        MeedlValidator.validateDataElement(loanee.getUserIdentity().getAlternatePhoneNumber());
    }

    public void validateForCreate() throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        MeedlValidator.validateObjectInstance(loanReferralStatus);
    }

}
