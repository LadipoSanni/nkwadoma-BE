package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum MeedlNotificationMessages {

    NEW_COHORT("New Cohort");


    private final String message;

    MeedlNotificationMessages(String message) {
        this.message = message;
    }
}
