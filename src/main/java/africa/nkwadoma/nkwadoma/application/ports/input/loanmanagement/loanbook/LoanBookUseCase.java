package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface LoanBookUseCase {
    @Async
    void upLoadUserData(LoanBook loanBook) throws MeedlException;
    @Async
    void uploadRepaymentHistory(LoanBook repaymentRecordBook) throws MeedlException;

    List<RepaymentHistory> getRepaymentsByEmail(List<RepaymentHistory> allRepayments, String email);
}
