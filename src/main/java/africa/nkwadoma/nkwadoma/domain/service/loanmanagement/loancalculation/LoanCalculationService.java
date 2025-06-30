package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LoanCalculationService {
    public BigDecimal calculateLoanAmountRequested(BigDecimal programFee, BigDecimal initialDeposit) throws MeedlException {
        validateAmount(programFee, "Program Fee");
        validateAmount(initialDeposit, "Initial Deposit");

        return programFee.subtract(initialDeposit);
    }
    public BigDecimal calculateLoanDisbursedOffered(BigDecimal loanAmountRequested, BigDecimal loanDisbursementFees) throws MeedlException {
        validateAmount(loanAmountRequested, "Loan Amount Requested");
        validateAmount(loanDisbursementFees, "Loan Disbursement Fees");

        return loanAmountRequested.add(loanDisbursementFees);
    }
    public int calculateMonthlyInterestRate(int interestRate) {
        validateInterestRate(interestRate);
        return interestRate / 12;
    }

    private void validateInterestRate(int interestRate) {
        if (interestRate < 0) {
            throw new IllegalArgumentException("Interest Rate must not be negative.");
        }
        if (interestRate > 100) {
            throw new IllegalArgumentException("Interest rate must not exceed 100.");
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
