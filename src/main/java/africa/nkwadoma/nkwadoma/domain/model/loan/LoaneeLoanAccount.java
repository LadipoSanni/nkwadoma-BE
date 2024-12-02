package africa.nkwadoma.nkwadoma.domain.model.loan;


import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.AccountStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoaneeLoanAccount {

    private String id;
    private AccountStatus status;
    private LoanStatus loanStatus;

    public void validate() throws MeedlException {
        MeedlValidator.validateObjectInstance(status);
        MeedlValidator.validateObjectInstance(loanStatus);
    }
}
