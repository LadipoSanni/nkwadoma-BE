package africa.nkwadoma.nkwadoma.domain.enums.constants.notification;

import lombok.Getter;

@Getter
public enum MeedlNotificationMessages {

    NEW_COHORT("New Cohort"),
    NEW_COHORT_CONTENT("just created a new cohort"),
    LOAN_OFFER("Loan Offered"),
    LOAN_OFFER_CONTENT("We are pleased to inform you that your loan application has been approved"),
    LOAN_REQUEST_DECLINED_CONTENT("Your loan request was declined"),
    LOAN_REQUEST("Loan Request"),
    NOTIFICATION_LIST_CANNOT_BE_EMPTY("Please select at least one notification to proceed."),
    LOAN_DEFERRAL_LOANEE("Your loan has been deferred "),
    LOAN_DEFERRAL("Loan Deferral"),
    DROP_OUT_LOANEE("We have received confirmation that you are no longer participating"),
    DROP_OUT("Dropped Out"),
    DROP_OUT_PORTFOLIO_MANAGER("Loanee has dropped out"),
    LOAN_DEFERRAL_PORTFOLIO_MANAGER("Loanee loan has been deffered"),
    DROP_OUT_BY_LOANEE("wanna drop out tired of this program");

    private final String message;

    MeedlNotificationMessages(String message) {
        this.message = message;
    }
}
