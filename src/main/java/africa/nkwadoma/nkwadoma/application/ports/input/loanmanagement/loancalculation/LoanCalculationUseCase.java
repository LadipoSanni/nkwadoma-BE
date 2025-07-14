package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.util.List;

public interface LoanCalculationUseCase {

    List<RepaymentHistory> sortRepaymentsByDateTimeAscending(List<RepaymentHistory> repayments)throws MeedlException;

    List<RepaymentHistory> calculateTotalRepaidment(
            List<RepaymentHistory> sortedRepayments,
            String loaneeId,
            String cohortId
    ) throws MeedlException;
}
