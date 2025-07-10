package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.util.List;

public interface AsynchronousLoanBookProcessingUseCase {
//    @Async
void upLoadUserData(LoanBook loanBook) throws MeedlException;

    void uploadRepaymentHistory(LoanBook repaymentRecordBook) throws MeedlException;

    List<RepaymentHistory> getRepaymentsByEmail(List<RepaymentHistory> allRepayments, String email);
}
