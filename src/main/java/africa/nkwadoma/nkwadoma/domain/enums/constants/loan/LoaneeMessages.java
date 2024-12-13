package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;


import lombok.Getter;

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
    LOANEE_MUST_BE_ADDED_TO_COHORT("Loanee Must Be Added To Cohort Before Referral");

    private final String message;

    LoaneeMessages(String message) {
        this.message = message;
    }
}
