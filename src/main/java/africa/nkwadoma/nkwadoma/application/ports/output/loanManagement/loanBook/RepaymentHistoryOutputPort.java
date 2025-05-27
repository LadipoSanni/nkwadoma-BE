package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

public interface RepaymentHistoryOutputPort {
    RepaymentHistory save(RepaymentHistory repaymentHistory);
}
