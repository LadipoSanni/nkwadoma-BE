package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;


import lombok.*;

@Getter
public enum LoaneeMessages {
    INVALID_LOANEE_ID("Please provide a valid loanee identification"),
    LOANEE_WITH_EMAIL_EXIST_IN_COHORT("Loanee With Email Exist In Cohort"),
    LOANEE_NOT_FOUND("Loanee Not Found"),
    LOANEE_HAS_BEEN_REFERRED("Loanee Has Been Referred"),
    LOANEE_REFERRAL_SUBJECT("Loan referral"),
    LOANEE_REFERRAL("loan-referral"),
    LOANEE_HAS_REFERRED("Loanee Has Been Referred"),
    LOANEE_REFERRAL_INVITATION_SENT("loanee-referral-invitation-sent"),
    LOANEE_MUST_BE_ADDED_TO_COHORT("Loanee Must Be Added To Cohort Before Referral"),
    LOANEE_CANNOT_BE_EMPTY("Loanee cannot be empty"),
    LOANEE_LOAN_DETAIL_CANNOT_BE_EMPTY("Loanee loan details must not be empty"),
    LOAN_REQUEST_STATUS_CANNOT_BE_EMPTY("Loan request status must not be empty"),
    LOANEE_HAS_BEEN_REFERRED_BEFORE("Loanee Has Been Referred Before"),
    LOANEE_MUST_EXIST("A loanee must exist for the loan request"),
    LOANEE_NAME_CANNOT_BE_EMPTY("Loanee name must not be empty"),;

    private final String message;

    LoaneeMessages(String message) {
        this.message = message;
    }
}
