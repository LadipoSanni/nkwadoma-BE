package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;

import java.util.List;

public interface LoanBookUseCase {
    LoanBook upLoadUserData(LoanBook loanBook) throws MeedlException;

    void uploadRepaymentRecord(LoanBook repaymentRecordBook) throws MeedlException;

}
