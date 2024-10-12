package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum MeedlMessages {
    EMPTY_INPUT_FIELD_ERROR("field cannot be null or empty"),
    INVALID_REQUEST("Request cannot be null or empty"),
    INVALID_EMAIL_ADDRESS("email address is not valid"),
    EMAIL_NOT_FOUND("email address cannot be found"),
    EMAIL_ALREADY_EXISTS("email address already exist"),
    EMAIL_INVITATION_SUBJECT("Invitation to Middl"),
    ORGANIZATION_INVITATION_TEMPLATE("organization-invitation"),
    COLLEAGUE_INVITATION_TEMPLATE("colleague-invitation"),
    DOMAIN_EMAIL_DOES_NOT_MATCH("domain email does not match"),
    EMAIL_INDEX("@"),
    INVALID_CREATED_BY("created by id not valid"), ;


    private final String message;

    MeedlMessages(String message) {
        this.message = message;
    }
}
