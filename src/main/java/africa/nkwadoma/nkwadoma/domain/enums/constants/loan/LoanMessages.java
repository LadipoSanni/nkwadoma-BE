package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanMessages {

    INVALID_LOAN_PRODUCT_REQUEST_DETAILS("Invalid or empty request details to create loan product "),
    INVALID_REQUEST("Invalid or empty request ");
    private final String message;

    LoanMessages(String message) {
        this.message = message;
    }
}
