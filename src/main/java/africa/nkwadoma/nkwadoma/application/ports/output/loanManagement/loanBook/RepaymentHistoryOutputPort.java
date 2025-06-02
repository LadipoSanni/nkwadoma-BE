package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

public interface RepaymentHistoryOutputPort {
    RepaymentHistory save(RepaymentHistory repaymentHistory) throws MeedlException;

    void delete(String repaymentId) throws MeedlException;
}
