package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import org.springframework.data.domain.Page;

public interface RepaymentHistoryOutputPort {
    RepaymentHistory save(RepaymentHistory repaymentHistory) throws MeedlException;

    void delete(String repaymentId) throws MeedlException;

    Page<RepaymentHistory> findRepaymentHistoryAttachedToALoaneeOrAll(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    RepaymentHistory findRepaymentHistoryById(String repaymentId) throws MeedlException;
}
