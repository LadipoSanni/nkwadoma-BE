package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoaneeLoanDetailMessages {
    INVALID_LOANEE_LOAN_DETAIL_ID("Please provide a valid Loanee LoanDetailId");

    private final String message;

    LoaneeLoanDetailMessages(String message) {
        this.message = message;
    }
}
