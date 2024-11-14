package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;


import lombok.Getter;

@Getter
public enum LoaneeMessages {

    LOANEE_WITH_EMAIL_EXIST_IN_COHORT("Loanee With Email Exist In Cohort"),
    LOANEE_NOT_FOUND("Loanee Not Found"),
    LOANEE_HAS_BEEN_REFERRED("Loanee Has Been Referred");

    private final String message;

    LoaneeMessages(String message) {
        this.message = message;
    }
}
