package africa.nkwadoma.nkwadoma.domain.constants;

import lombok.Getter;

@Getter
public enum MiddlMessages {
    EMPTY_INPUT_FIELD_ERROR("field cannot be null or empty"),
    INVALID_EMAIL_ADDRESS("email address is not valid"),
    EMAIL_NOT_FOUND("email address cannot be found"),
    EMAIL_ALREADY_EXISTS("email address already exist"),
    EMAIL_INVITATION_SUBJECT("Invitation to Middl"),
    ORGANIZATION_INVITATION_TEMPLATE("organization-invitation"),
    DOMAIN_EMAIL_DOES_NOT_MATCH("domain email does not match"),
    EMAIL_INDEX("@");

    private final String message;

    MiddlMessages(String message) {
        this.message = message;
    }
}
