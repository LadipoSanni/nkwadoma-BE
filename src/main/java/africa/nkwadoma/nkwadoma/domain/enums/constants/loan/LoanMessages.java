package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;

import lombok.*;

@Getter
public enum LoanMessages {

    INVALID_STATUS("Invalid status provided"),
    INVALID_LOAN_PRODUCT_REQUEST_DETAILS("Invalid or empty request details to create loan product "),
    INVALID_LOAN_PRODUCT_SIZE("Loan product size can not be 0 or less"),
    INVALID_OBLIGOR_LIMIT("Obligor limit can not be 0 or less"),
    INVALID_REQUEST("Invalid or empty request "),
    OBLIGOR_LIMIT_GREATER_THAN_PRODUCT_SIZE("Obligor limit is greater than loan product size"),
    LOAN_PRODUCT_SIZE_REQUIRED("Loan product size is required"),
    OBLIGOR_LOAN_LIMIT_REQUIRED("Obligor loan limit is required"),
    TENOR_IS_REQUIRED("Tenor is required"),
    MORATORIUM_BELOW_BOUND("Moratorium cannot be below 1"),
    MORATORIUM_ABOVE_BOUND("Moratorium cannot be above 24"),
    LOAN_PRODUCT_NAME_REQUIRED("Loan product name is required"),
    LOAN_PRODUCT_REQUIRED("Loan product cannot be empty"),
    MINIMUM_REPAYMENT_AMOUNT_REQUIRED("Minimum repayment amount is required"),
    TERMS_AND_CONDITIONS_REQUIRED("Loan terms and conditions is required"),
    LOAN_INTEREST_RATE_REQUIRED("Loan interest rate is required"),
    SPONSOR_REQUIRED("Please select the sponsor(s) of this loan"),
    MANDATE_REQUIRED("Loan mandate is required"),
    FUND_PRODUCT_ID_REQUIRED("Please select a fund product."),
    LOAN_ID_REQUIRED("Loan id is required"),
    INVALID_LOAN_PRODUCT_ID("Please provide a valid loan product identification"),
    INVALID_LOANEE_ID("Please provide a valid loanee identification"),
    INVALID_LOAN_ID("Please provide a valid loan identification"),
    TENOR_STATUS_MONTH_BOND("Tenor should be between 1 TO 120 months"),
    LOAN_REFERRAL_STATUS_MUST_BE_ACCEPTED("Loan referral must be accepted"),
    LOAN_REFERRAL_NOT_FOUND("Loan referral not found"),
    LOAN_REQUEST_NOT_FOUND("Loan request not found"),
    LOAN_REQUEST_MUST_HAVE_BEEN_APPROVED("Loan Request Must Have Been Approved"),
    LOANEE_NOT_FOUND("Loanee not found"),
    LOAN_AMOUNT_APPROVED_MUST_BE_LESS_THAN_OR_EQUAL_TO_REQUESTED_AMOUNT("Loan amount approved must be less than or equal to the loan amount requested"),
    LOAN_PRODUCT_NOT_FOUND("Loan product not found"),
    LOAN_REQUEST_STATUS_MUST_BE_NEW("Loan request status must be new"),
    LOAN_REQUEST_HAS_ALREADY_BEEN_APPROVED("Loan request has already been approved"),
    ADDITIONAL_DETAILS_REQUIRED("Loanee must have provided additional details before loan referral can be accepted"),
    MULTIPLE_LOAN_REFERRALS_IS_CURRENTLY_NOT_ALLOWED("Multiple loan referrals is currently not allowed"),
    LOAN_REFERRAL_CANNOT_BE_EMPTY("Loan referral cannot be empty"),
    LOAN_REQUEST_CANNOT_BE_EMPTY("Loan request cannot be empty"),
    LOAN_AMOUNT_REQUESTED_MUST_NOT_BE_EMPTY("Loan amount requested must not be empty"),
    LOAN_REFERRAL_STATUS_CANNOT_BE_EMPTY("Loan referral status must not be empty"),
    LOAN_REFERRAL_ID_MUST_NOT_BE_EMPTY("Loan referral ID must not be empty"),
    ACCEPTANCE_TIME_FRAME_PASSED("Acceptance Time Frame Passed"),
    LOAN_OFFER_NOT_ASSIGNED_TO_LOANEE("Loan Offer Not Assigned To Loanee"),
    LOAN_OFFER_NOT_FOUND("Loan Offer Not Found"),
    REASON_IS_REQUIRED("Reason for declining loan request is required"),
    INVALID_LOAN_DECISION(   "Loan decision must either be ACCEPTED or DECLINED"),
    LOAN_REQUEST_ID_CANNOT_BE_EMPTY("Loan request ID must not be empty"),
    LOAN_CANNOT_BE_EMPTY("Loan cannot be empty"),
    LOAN_BREAK_DOWN_CANNOT_BE_EMPTY("Loan break down cannot be empty"),
    LOANEE_ACCOUNT_NOT_FOUND("Loan account not found"),
    LOAN_METRICS_MUST_NOT_BE_EMPTY("Loan metrics object must not be empty"),
    LOAN_START_DATE_MUST_NOT_BE_EMPTY("Loan start date must not be empty"),
    LOAN_REQUEST_MUST_NOT_BE_EMPTY("Loan request must not be empty"), REASON_FOR_DECLINING_IS_REQUIRED("Reason for declining is required"),
    LOAN_AMOUNT_APPROVED_MUST_NOT_BE_EMPTY("Loan amount approved must not be empty"),
    LOAN_OFFER_DECISION_MADE("Loan offer decision made previously"),
    LOAN_NOT_FOUND("Loan not found"),
    LOAN_REFERRAL_HAS_ALREADY_BEEN_ACCEPTED("Loan referral has already been accepted"),
    LOAN_REFERRAL_HAS_ALREADY_BEEN_DECLINED("Loan referral has already been declined"),
    INVALID_LOAN_REFERRAL_ID("Loan referral ID must not be empty"),
    LOAN_ALREADY_EXISTS_FOR_THIS_LOANEE("Loan already exists for this loanee"),
    CANNOT_START_LOAN_FOR_LOAN_OFFER_THAT_AS_BEEN_DECLINED("Can not start loan for loan offer that as been declined"),
    LOAN_REQUEST_CANNOT_BE_APPROVED("Loanee identity not verified, loanee request cannot be approved "),
    LOANEE_LOAN_NOT_FOUND("Loanee loan not found"),
    LOAN_REFERRAL_NOT_ASSIGNED_TO_LOANEE("Loan referral not assigned to loanee"),
    LOAN_DECISION("Loan offer decision hasn't been made yet "),
    LOAN_REFERRAL_DECISION_CANNOT_BE_MADE_ON_ANOTHER_LOANEE_REFERRAL(
            "Loanee cannot make a decision on this loan referral, it doesn't belong to this loanee"
    ),
    LOAN_DOES_NOT_BELONG_TO_LOANEE("This loan does not belong to this loanee");
    private final String message;

    LoanMessages(String message) {
        this.message = message;
    }
}
