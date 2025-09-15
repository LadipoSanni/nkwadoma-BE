package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.*;

@Getter
public enum MeedlMessages {
    EMPTY_INPUT_FIELD_ERROR("Field cannot be empty"),
    TOKEN_REQUIRED("User validation mechanism in form of token is required"),
    INVALID_EMAIL_ADDRESS("Email address is not valid"),
    USER_ID_CANNOT_BE_EMPTY("User id cannot be empty"),
    INVALID_INDUSTRY_OR_SERVICE_OFFERING("Industry or service offering cannot be empty"),
    EMAIL_NOT_FOUND("Email address cannot be found"),
    EMAIL_ALREADY_EXISTS("Email address already exist"),
    EMAIL_INVITATION_SUBJECT("Invitation to Meedl"),
    RESET_PASSWORD("Reset Password"),
    ORGANIZATION_INVITATION_TEMPLATE("organization-invitation"),
    COLLEAGUE_INVITATION_TEMPLATE("colleague-invitation"),
    DOMAIN_EMAIL_DOES_NOT_MATCH("Domain email does not match"),
    EMAIL_INDEX("@"),
    INVALID_OBJECT("Object not found"),
    UUID_NOT_VALID("UUID not valid"),
    NON_EXISTING_CREATED_BY("Creator not found"),
    INVALID_CREATED_BY_ID("Please provide a valid identification for the user performing this action."),
    USER_NOT_ENABLED("User not enabled"),
    PAGE_NUMBER_CANNOT_BE_LESS_THAN_ZERO("Page number must not be less than zero"),
    PAGE_SIZE_CANNOT_BE_LESS_THAN_ONE("Page size must not be less than one"),
    FORGOT_PASSWORD_TEMPLATE("forget-password"),
    LOANEE_REFERRAL_SUBJECT("Loan referral"),
    LOANEE_REFERRAL("loan-referral"),
    LOANEE_HAS_REFERRED("Loanee has been referred"),
    LOANEE_REFERRAL_INVITATION_SENT("loanee-referral-invitation-sent"),
    INVALID_TIN("Tax identity number must contain 9 - 15 characters and can only have a hyphen special character."),
    TIN_CANNOT_BE_EMPTY("Tax identity number cannot be empty"), CREATED_AT("createdAt"),
    INVALID_SEARCH_PARAMETER("Please provide a valid search name."),
    DEACTIVATE_ORGANIZATION("Deactivate organization"),
    DEACTIVATE_ORGANIZATION_TEMPLATE("deactivate-organization"),;


    private final String message;

    MeedlMessages(String message) {
        this.message = message;
    }
}
