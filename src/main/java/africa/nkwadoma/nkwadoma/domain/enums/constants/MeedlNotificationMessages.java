package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum MeedlNotificationMessages {

    NEW_COHORT("New Cohort"),
    NEW_COHORT_CONTENT("just created a new cohort"),
    LOAN_OFFER("Loan Offer"),
    LOAN_OFFER_CONTENT("We are pleased to inform you that your loan application has been approved!"),
    NOTIFICATION_LIST_CANNOT_BE_EMPTY("Notification list cannot be empty");


    private final String message;

    MeedlNotificationMessages(String message) {
        this.message = message;
    }
}
