package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.util.List;

public interface RepaymentHistoryUseCase {
    List<RepaymentHistory> saveCohortRepaymentHistory(LoanBook loanBook) throws MeedlException;
}
