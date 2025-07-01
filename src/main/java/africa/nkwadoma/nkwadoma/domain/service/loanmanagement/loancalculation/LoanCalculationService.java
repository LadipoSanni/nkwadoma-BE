package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.LoanCalculationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
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
    @Override
    public BigDecimal calculateManagementFee(BigDecimal loanAmountRequested, double mgtFeeInPercentage) throws MeedlException {
        validateAmount(loanAmountRequested, "Loan Amount Requested");
        validateInterestRate(mgtFeeInPercentage, "Management Fee Percentage");

        BigDecimal percentageDecimal = BigDecimal.valueOf(mgtFeeInPercentage).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        return loanAmountRequested.multiply(percentageDecimal).setScale(8, RoundingMode.HALF_UP);
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
            log.error("{} must not be negative. In validate interest rate.",name);
            throw new MeedlException(name+" must not be negative.");
        }
        if (interestRate > 100) {
            log.error("{} must not exceed 100. In validate interest rate.", name );
            throw new MeedlException(name+" must not exceed 100.");
        }
    }

    private void validateInterestRate(double interestRate, String name) throws MeedlException {
        if (interestRate < 0) {
            log.error("{} must not be negative. In validate interest rate as double",name);
            throw new MeedlException(name+" must not be negative.");
        }
        if (interestRate > 100) {
            log.error("{} must not exceed 100. In validate interest rate. as double", name );
            throw new MeedlException(name+" must not exceed 100.");
        }
    }



    private void validateAmount(BigDecimal amount, String name) throws MeedlException {
        if (amount == null) {
            log.error( "{} must not be null. In validate amount", name);
            throw new MeedlException(name + " must not be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            log.error( "{} must not be negative. In validate amount", name);
            throw new MeedlException(name + " must not be negative.");
        }

    }
}
