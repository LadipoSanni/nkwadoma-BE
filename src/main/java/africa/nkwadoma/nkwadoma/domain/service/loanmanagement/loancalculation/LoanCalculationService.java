package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.LoanCalculationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import org.apache.commons.math3.stat.StatUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Component
public class LoanCalculationService implements LoanCalculationUseCase {
    @Override
    public BigDecimal calculateLoanAmountRequested(BigDecimal programFee, BigDecimal initialDeposit) throws MeedlException {
        validateAmount(programFee, "Program Fee");
        validateAmount(initialDeposit, "Initial Deposit");

        return programFee.subtract(initialDeposit);
    }
    @Override
    public BigDecimal calculateLoanAmountDisbursed(BigDecimal loanAmountRequested, BigDecimal loanDisbursementFees) throws MeedlException {
        validateAmount(loanAmountRequested, "Loan Amount Requested");
        validateAmount(loanDisbursementFees, "Loan Disbursement Fees");

        return loanAmountRequested.add(loanDisbursementFees);
    }

    @Override
    public BigDecimal calculateLoanAmountOutstanding(
            BigDecimal loanAmountOutstanding,
            BigDecimal monthlyRepayment,
            int moneyWeightedPeriodicInterest
    ) throws MeedlException {
        validateAmount(loanAmountOutstanding, "Loan Amount Outstanding");
        validateAmount(monthlyRepayment, "Monthly Repayment");
        validateInterestRate(moneyWeightedPeriodicInterest, "Money Weighted Periodic Interest");

        return loanAmountOutstanding
                .subtract(monthlyRepayment)
                .add(BigDecimal.valueOf(moneyWeightedPeriodicInterest));
    }
    @Override
    public BigDecimal calculateMoneyWeightedPeriodicInterest(int interestRate, List<LoanPeriodRecord> periods) throws MeedlException {
        validateInterestRate(interestRate, "Interest rate");

        BigDecimal sumProduct = BigDecimal.ZERO;
        for (LoanPeriodRecord record : periods) {
            validateLoanPeriodRecord(record);
            BigDecimal product = record.getLoanAmountOutstanding().multiply(BigDecimal.valueOf(record.getDaysHeld()));
            sumProduct = sumProduct.add(product);
        }

        BigDecimal rateFraction = BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(365), 8, RoundingMode.HALF_UP);
        return rateFraction.multiply(sumProduct);
    }

    @Override
    public int calculateMonthlyInterestRate(int interestRate) throws MeedlException {
        validateInterestRate(interestRate, "Interest rate");
        return interestRate / 12;
    }

    public BigDecimal calculateLoanDisbursementFees(Map<String, BigDecimal> feeMap) throws MeedlException {
        if (feeMap == null || feeMap.isEmpty()) {
            return BigDecimal.ZERO.setScale(8, RoundingMode.HALF_UP);
        }

        double[] values = new double[feeMap.size()];
        int i = 0;
        for (Map.Entry<String, BigDecimal> entry : feeMap.entrySet()) {
            String name = entry.getKey();
            BigDecimal value = entry.getValue();

            if (value == null) throw new MeedlException(name + " must not be null.");
            if (value.compareTo(BigDecimal.ZERO) < 0) throw new MeedlException(name + " must not be negative.");

            values[i++] = value.doubleValue();
        }

        double sum = StatUtils.sum(values);
        return BigDecimal.valueOf(sum).setScale(2, RoundingMode.HALF_UP);
    }


    private void validateLoanPeriodRecord(LoanPeriodRecord record) throws MeedlException {
        if (record == null){
            throw new MeedlException("Loan period record must not be null.");
        }
        if (record.getLoanAmountOutstanding() == null) {
            throw new MeedlException("Loan Amount Outstanding must not be null.");
        }
        if (record.getLoanAmountOutstanding().compareTo(BigDecimal.ZERO) < 0) {
            throw new MeedlException("Loan Amount Outstanding must not be negative.");
        }
        if (record.getDaysHeld() < 0) {
            throw new MeedlException("Days Held must not be negative.");
        }
    }

    private void validateInterestRate(int interestRate, String name) throws MeedlException {
        if (interestRate < 0) {
            throw new MeedlException(name+" must not be negative.");
        }
        if (interestRate > 100) {
            throw new MeedlException(name+" must not exceed 100.");
        }
    }

    private void validateAmount(BigDecimal amount, String name) throws MeedlException {
        if (amount == null) {
            throw new MeedlException(name + " must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new MeedlException(name + " must not be negative.");
        }

    }
}
