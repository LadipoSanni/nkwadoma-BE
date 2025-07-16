package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;

public interface LoaneeLoanAccountOutputPort {
    LoaneeLoanAccount save(LoaneeLoanAccount loaneeLoanAccount) throws MeedlException;

    void deleteLoaneeLoanAccount(String loaneeLoanAccountId) throws MeedlException;

    LoaneeLoanAccount findByLoaneeId(String loaneeId) throws MeedlException;


}
