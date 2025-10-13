package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanProductMessage {
    LOAN_PRODUCT_SIZE_REQUIRED("Loan product size is required"),
    INVALID_LOAN_PRODUCT_REQUEST_DETAILS("Invalid or empty request details to create loan product "),
    LOAN_PRODUCT_REQUIRED("Loan product cannot be empty"),
    INVALID_LOAN_PRODUCT_SIZE("Loan product size can not be 0 or less"),
    INVALID_OBLIGOR_LIMIT("Obligor limit can not be 0 or less"),
    INVALID_LOAN_PRODUCT_ID("Please provide a valid loan product identification");

    private final String message;

    LoanProductMessage(String message) {
        this.message = message;
    }
}
