package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface LoanCalculationUseCase {

    List<RepaymentHistory> sortRepaymentsByDateDescending(List<RepaymentHistory> repayments)throws MeedlException;
}
