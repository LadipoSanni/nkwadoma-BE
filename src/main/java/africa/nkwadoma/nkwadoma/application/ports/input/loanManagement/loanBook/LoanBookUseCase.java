package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentRecordBook;

public interface LoanBookUseCase {
    LoanBook upLoadFile(LoanBook loanBook) throws MeedlException;

    void uploadRepaymentRecord(LoanBook repaymentRecordBook) throws MeedlException;
}
