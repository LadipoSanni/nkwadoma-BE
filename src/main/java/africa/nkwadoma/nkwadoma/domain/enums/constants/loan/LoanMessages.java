package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.Getter;

@Getter
public enum LoanMessages {

    INVALID_STATUS ("Invalid status provided"),
    INVALID_LOAN_PRODUCT_REQUEST_DETAILS("Invalid or empty request details to create loan product "),
    INVALID_LOAN_PRODUCT_SIZE("Loan product size can not be 0 or less"),
    INVALID_OBLIGOR_LIMIT("Obligor limit can not be 0 or less"),
    INVALID_REQUEST("Invalid or empty request "),
    OBLIGOR_LIMIT_GREATER_THAN_PRODUCT_SIZE("Obligor limit is greater than loan product size"),
    LOAN_PRODUCT_SIZE_REQUIRED("Loan product size is required"),
    OBLIGOR_LOAN_LIMIT_REQUIRED("Obligor loan limit is required"),
    TENOR_IS_REQUIRED("Tenor is required"),
    MORATORIUM_BELOW_BOUND ("Moratorium cannot be below 1"),
    MORATORIUM_ABOVE_BOUND ("Moratorium cannot be above 24"),
    LOAN_PRODUCT_NAME_REQUIRED ("Loan product name is required"),
    MINIMUM_REPAYMENT_AMOUNT_REQUIRED ("Minimum repayment amount is required"),
    TERMS_AND_CONDITIONS_REQUIRED ("Loan terms and conditions is required"),
    LOAN_INTEREST_RATE_REQUIRED ("Loan interest rate is required"),
    SPONSOR_REQUIRED("Please select the sponsor(s) of this loan"),
    MANDATE_REQUIRED("Loan mandate is required"),
    FUND_PRODUCT_ID_REQUIRED("Please select a fund product."),
    LOAN_ID_REQUIRED("Loan id is required"),
    TENOR_STATUS_MONTH_BOND("Tenor should be between 1 TO 120 months"),
    LOAN_REFERRAL_STATUS_MUST_BE_ACCEPTED("Loan referral must be accepted"),
    LOAN_REFERRAL_NOT_FOUND("Loan referral not found"),
    LOAN_REQUEST_NOT_FOUND("Loan request not found"),
    LOAN_REQUEST_MUST_HAVE_BEEN_APPROVED("Loan Request Must Have Been Approved"),
    LOANEE_NOT_FOUND("Loanee not found"),;
    private final String message;

    LoanMessages(String message) {
        this.message = message;
    }
}
