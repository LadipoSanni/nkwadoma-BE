package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.AccountStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.*;

@Getter
@Setter
@ToString
public class LoaneeLoanAccount {
    private String id;
    private AccountStatus accountStatus;
    private LoanStatus loanStatus;
    private String loaneeId;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(accountStatus);
        MeedlValidator.validateObjectInstance(loanStatus);
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
    }
}
