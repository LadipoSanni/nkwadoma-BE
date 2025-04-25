package africa.nkwadoma.nkwadoma.domain.enums.constants.notification;

import lombok.Getter;

@Getter
public enum MeedlNotificationMessages {

    NEW_COHORT("New Cohort"),
    NEW_COHORT_CONTENT("just created a new cohort"),
    LOAN_OFFER("Loan Offer"),
    LOAN_OFFER_CONTENT("We are pleased to inform you that your loan application has been approved!"),
    LOAN_REQUEST_DECLINED_CONTENT("Your loan request was declined"),
    LOAN_REQUEST("Loan Request"),
    NOTIFICATION_LIST_CANNOT_BE_EMPTY("Please select at least one notification to proceed.");

    private final String message;

    MeedlNotificationMessages(String message) {
        this.message = message;
    }
}
