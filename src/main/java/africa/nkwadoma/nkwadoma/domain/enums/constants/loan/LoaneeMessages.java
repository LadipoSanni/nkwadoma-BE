package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;


import lombok.*;

@Getter
public enum LoaneeMessages {
    INVALID_LOANEE_ID("Please provide a valid loanee identification"),
    INVALID_ONBOARDING_MODE("Specify mode of onboarding."),
    LOANEE_WITH_EMAIL_EXIST_IN_COHORT("Loanee with email exist in cohort"),
    LOANEE_NOT_FOUND("Loanee Not Found"),
    LOANEE_HAS_BEEN_REFERRED("Loanee has been referred"),
    LOANEE_REFERRAL_SUBJECT("Loan referral"),
    LOANEE_REFERRAL("loan-referral"),
    LOANEE_REFERRAL_INVITATION_SENT("loanee-referral-invitation-sent"),
    LOANEE_MUST_BE_ADDED_TO_COHORT("Loanee must be added to cohort before referral"),
    LOANEE_CANNOT_BE_EMPTY("Loanee cannot be empty"),
    LOANEE_LOAN_DETAIL_CANNOT_BE_EMPTY("Loanee loan details must not be empty"),
    LOAN_REQUEST_STATUS_CANNOT_BE_EMPTY("Loan request status must not be empty"),
    LOANEE_HAS_BEEN_REFERRED_BEFORE("Loanee has been referred before"),
    LOANEE_MUST_EXIST("A loanee must exist for the loan request"),
    LOANEE_NAME_CANNOT_BE_EMPTY("Loanee name must not be empty"),
    LOAN_REQUEST_APPROVAL("loan-request-approval"),
    LOAN_OFFER_ACCEPTED("Loan offer accepted"),
    LOAN_OFFER_DECLINED("Loan offer declined"),
    LOAN_OFFER_ACCEPTED_TEMPLATE("loan-offer-accepted"),
    LOAN_OFFER_DECLINED_TEMPLATE("loan-offer-declined"),
    LOAN_REQUEST_APPROVED("loan request approved"),
    PLEASE_PROVIDE_A_VALID_LOANEE_IDENTIFICATION("Please provide a valid loanee identification"),
    LOANEE_WITH_ZERO_OR_NEGATIVE_AMOUNT_REQUEST_CANNOT_BE_ADDED_TO_COHORT("Loanee with zero or negative amount request " +
            "cannot be added to cohort"),
    LOANEE_NOT_ASSOCIATE_WITH_ORGANIZATION("Loanee does not associated with a cohort that belong to your organization "),
    LOANEE_DOES_NOT_EXIST_IN_COHORT("Loanee does not exist in cohort"),
    LOANEE_CANNOT_DROP_FROM_COHORT("Loanee cannot drop from cohort"),
    LOANEE_CANNOT_DEFER_LOAN("Loanee cannot defer loan"),
    LOANEES_ID_CANNOT_BE_EMPTY("Loanees cannot ID be empty"),
    LOAN_REFERRAL_ID_CANNOT_BE_EMPTY("Loan referral cannot ID be empty"),;

    private final String message;

    LoaneeMessages(String message) {
        this.message = message;
    }
}
