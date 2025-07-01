package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;

import java.math.BigDecimal;
import java.util.List;

public interface LoanCalculationUseCase {
    BigDecimal calculateLoanAmountRequested(BigDecimal programFee, BigDecimal initialDeposit) throws MeedlException;

    BigDecimal calculateLoanAmountDisbursed(BigDecimal loanAmountRequested, BigDecimal loanDisbursementFees) throws MeedlException;

    BigDecimal calculateLoanAmountOutstanding(
            BigDecimal loanAmountOutstanding,
            BigDecimal monthlyRepayment,
            int moneyWeightedPeriodicInterest
    ) throws MeedlException;

    BigDecimal calculateMoneyWeightedPeriodicInterest(int interestRate, List<LoanPeriodRecord> periods) throws MeedlException;

    int calculateMonthlyInterestRate(int interestRate) throws MeedlException;

    BigDecimal calculateManagementFee(BigDecimal loanAmountRequested, double mgtFeeInPercentage) throws MeedlException;
}
