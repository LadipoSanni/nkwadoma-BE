package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;


import lombok.Getter;

@Getter
public enum LoaneeMessages {

    LOANEE_WITH_EMAIL_EXIST_IN_COHORT("Loanee With Email Exist In Cohort");

    private final String message;

    LoaneeMessages(String message) {
        this.message = message;
    }
}
