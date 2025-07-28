package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.CalculationContext;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.math.BigDecimal;
import java.util.List;

public interface CalculationEngineUseCase {

    List<RepaymentHistory> sortRepaymentsByDateTimeAscending(List<RepaymentHistory> repayments)throws MeedlException;

    void calculateLoaneeLoanRepaymentHistory(CalculationContext calculationContext) throws MeedlException;

    BigDecimal calculateCurrentAmountPaid(List<RepaymentHistory> repaymentHistories);
}
