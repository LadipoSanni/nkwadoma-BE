package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum MeedlMessages {
    EMPTY_INPUT_FIELD_ERROR("Field cannot be null or empty"),
    INVALID_REQUEST("Request cannot be null or empty"),
    INVALID_EMAIL_ADDRESS("Email address is not valid"),
    INVALID_INDUSTRY_OR_SERVICE_OFFERING("Industry or service offering cannot be empty"),
    EMAIL_NOT_FOUND("Email address cannot be found"),
    EMAIL_ALREADY_EXISTS("Email address already exist"),
    EMAIL_INVITATION_SUBJECT("Invitation to Middl"),
    RESET_PASSWORD("Reset Password"),
    ORGANIZATION_INVITATION_TEMPLATE("organization-invitation"),
    COLLEAGUE_INVITATION_TEMPLATE("colleague-invitation"),
    DOMAIN_EMAIL_DOES_NOT_MATCH("Domain email does not match"),
    EMAIL_INDEX("@"),
    INVALID_CREATED_BY("Created by id not valid"),
    INVALID_OBJECT("Object not found"),
    UUID_NOT_VALID("UUID not valid"),
    NON_EXISTING_CREATED_BY("Creator not found"),
    USER_NOT_ENABLED("User not enabled"),
    PAGE_NUMBER_CANNOT_BE_LESS_THAN_ZERO("Page number must not be less than zero"),
    PAGE_SIZE_CANNOT_BE_LESS_THAN_ONE("Page size must not be less than one"),
    LOANEE_REFERRAL_SUBJECT("Loan referral"),
    LOANEE_REFERRAL("loan-referral"),
    LOANEE_HAS_REFERRED("Loanee Has Been Referred"),
    LOANEE_REFERRAL_INVITATION_SENT("loanee-referral-invitation-sent");


    private final String message;

    MeedlMessages(String message) {
        this.message = message;
    }
}
