package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RepaymentHistoryOutputPort {
    RepaymentHistory save(RepaymentHistory repaymentHistory) throws MeedlException;

    void delete(String repaymentId) throws MeedlException;

    Page<RepaymentHistory>  findRepaymentHistoryAttachedToALoaneeOrAll(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    RepaymentHistory getFirstAndLastYear(String loaneeId) ;

    RepaymentHistory findRepaymentHistoryById(String repaymentId) throws MeedlException;

    Page<RepaymentHistory> searchRepaymemtHistoryByLoaneeName(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    RepaymentHistory findLatestRepayment(String loaneeId, String cohortId) throws MeedlException;

    List<RepaymentHistory> findAllRepaymentHistoryForLoan(String loaneeId, String cohortId) throws MeedlException;

    void deleteMultipleRepaymentHistory(List<String> repaymentHistoryIds);

    List<RepaymentHistory> saveAllRepaymentHistory(List<RepaymentHistory> currentRepaymentHistories);

    boolean checkIfLoaneeHasMadeAnyRepayment(String id, String cohortId) throws MeedlException;

    Page<RepaymentHistory> findAllRepaymentHistoryByLoanId(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    RepaymentHistory getFirstAndLastYearOfLoanRepayment(String loanId) throws MeedlException;
}
