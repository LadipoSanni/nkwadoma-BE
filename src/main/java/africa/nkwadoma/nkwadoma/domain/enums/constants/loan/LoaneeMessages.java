package africa.nkwadoma.nkwadoma.domain.enums.constants.loan;


import lombok.Getter;

@Getter
public enum LoaneeMessages {

    LOANEE_WITH_EMAIL_EXIST("Loanee With Email Exist");

    private final String message;

    LoaneeMessages(String message) {
        this.message = message;
    }
}
