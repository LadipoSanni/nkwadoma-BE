package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum MeedlNotificationMessages {

    NEW_COHORT("New Cohort"),
    LOAN_OFFER("Loan Offer"),
    LOAN_OFFER_CONTENT("We are pleased to inform you that your loan application has been approved!");


    private final String message;

    MeedlNotificationMessages(String message) {
        this.message = message;
    }
}
