package africa.nkwadoma.nkwadoma.domain.enums.constants.notification;

import lombok.Getter;

@Getter
public enum ContextMessages {
    CONTEXT_TOKEN("token"),
    CONTEXT_CURRENT_YEAR("currentYear"),
    CONTEXT_ORGANIZATION_NAME("organizationName"),
    CONTEXT_LINK("loanOfferId"),
    CONTEXT_FIRST_NAME("firstName"),
    CONTEXT_VEHICLE_NAME("vehicleName"),
    CONTEXT_DEACTIVATION_REASON("deactivationReason"),;

    public String format(Object... args) {
        return String.format(message, args);
    }

    private final String message;


    ContextMessages(String message) {
        this.message = message;
    }
}
