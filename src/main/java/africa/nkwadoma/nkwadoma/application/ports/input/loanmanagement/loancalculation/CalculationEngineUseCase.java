package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.math.BigDecimal;
import java.util.List;

public interface CalculationEngineUseCase {

    List<RepaymentHistory> sortRepaymentsByDateTimeAscending(List<RepaymentHistory> repayments)throws MeedlException;

    BigDecimal calculateTotalRepayment(
            List<RepaymentHistory> sortedRepayments,
            String loaneeId,
            String cohortId
    ) throws MeedlException;
}
