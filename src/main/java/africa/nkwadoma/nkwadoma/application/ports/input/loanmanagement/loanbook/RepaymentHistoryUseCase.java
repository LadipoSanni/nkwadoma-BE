package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface RepaymentHistoryUseCase {

    Page<RepaymentHistory>  findAllRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    Page<RepaymentHistory> searchRepaymentHistory(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException;

    RepaymentHistory getFirstRepaymentYearAndLastRepaymentYear(String actorId,String loaneeId) throws MeedlException;

    List<RepaymentHistory> generateRepaymentHistory(BigDecimal amountApproved, String loanProductId, String loanId) throws MeedlException;

    RepaymentHistory simulateRepayment(BigDecimal loanAmount, double interestRate, int repaymentPeriod) throws MeedlException;

    Page<RepaymentHistory> findAllRepaymentHistoryByLoanId(RepaymentHistory repaymentHistory,  int pageSize, int pageNumber) throws MeedlException;
}
