package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;

public interface LoanBookUseCase {
    LoanBook upLoadUserData(LoanBook loanBook) throws MeedlException;

    void uploadRepaymentRecord(LoanBook repaymentRecordBook) throws MeedlException;
}
