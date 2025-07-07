package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanCalculationMessages {

    PAYMENT_DATE_CANNOT_BE_NULL("Payment date must be provided"),
    REPAYMENT_HISTORY_MUST_BE_PROVIDED("Repayment history must be provided.");

    private final String message;
    LoanCalculationMessages(String message) {
        this.message = message;
    }
}
