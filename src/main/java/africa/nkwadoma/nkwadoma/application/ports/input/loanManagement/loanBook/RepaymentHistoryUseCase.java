package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RepaymentHistoryUseCase {
    List<RepaymentHistory> saveCohortRepaymentHistory(LoanBook loanBook) throws MeedlException;

    Page<RepaymentHistory>  findAllRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    Page<RepaymentHistory> searchRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    RepaymentHistory getFirstRepaymentYearAndLastRepaymentYear(String actorId,String loaneeId) throws MeedlException;
}
