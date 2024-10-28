package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum MeedlMessages {
    EMPTY_INPUT_FIELD_ERROR("field cannot be null or empty"),
    INVALID_REQUEST("Request cannot be null or empty"),
    INVALID_EMAIL_ADDRESS("email address is not valid"),
    INVALID_INDUSTRY_OR_SERVICE_OFFERING("Industry or service offering cannot be empty"),
    EMAIL_NOT_FOUND("email address cannot be found"),
    EMAIL_ALREADY_EXISTS("email address already exist"),
    EMAIL_INVITATION_SUBJECT("Invitation to Middl"),
    ORGANIZATION_INVITATION_TEMPLATE("organization-invitation"),
    COLLEAGUE_INVITATION_TEMPLATE("colleague-invitation"),
    DOMAIN_EMAIL_DOES_NOT_MATCH("domain email does not match"),
    EMAIL_INDEX("@"),
    INVALID_CREATED_BY("created by id not valid"),
    INVALID_OBJECT("Object not found"),
    NON_EXISTING_CREATED_BY("Creator not found"),
    UUID_NOT_VALID("uuid not valid"),
    USER_NOT_ENABLED("User not enabled"),
    PAGE_NUMBER_CANNOT_BE_LESS_THAN_ZERO("Page number must not be less than zero"),
    PAGE_SIZE_CANNOT_BE_LESS_THAN_ONE("Page size must not be less than one");


    private final String message;

    MeedlMessages(String message) {
        this.message = message;
    }
}
