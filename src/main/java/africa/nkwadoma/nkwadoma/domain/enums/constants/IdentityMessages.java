package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum IdentityMessages{

    USER_NOT_FOUND("User not found!"),
    CLIENT_NOT_FOUND("Client not found!"),
    COLLEAGUE_EXIST("colleague exist!"),
    USER_IDENTITY_CANNOT_BE_NULL("User identity cannot be null"),
    ORGANIZATION_IDENTITY_CANNOT_BE_NULL("Organization identity cannot be null"),
    RC_NUMBER_NOT_FOUND("Rc number not found!"),
    ORGANIZATION_NOT_FOUND("Organization not found"),
    CLIENT_EXIST("Client already exists"),
    INVALID_REGISTRATION_DETAILS("Invalid registration details"),
    INVALID_VALID_ROLE("Role is required"),
    USER_IDENTITY_ALREADY_EXISTS("UserIdentity already exists"),
    ENCODING_VALUE("utf-8"),
    CONTEXT_TOKEN("token"),
    CONTEXT_FIRST_NAME("firstName"),
    CONTEXT_CURRENT_YEAR("currentYear"),
    PASSWORD_HAS_BEEN_CREATED("Password has been created Already"),
    PASSWORD_PATTERN("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$"),
    WEAK_PASSWORD("Password should be up to 8 characters and must contain at least 1 alphabet, number and special characters."),
    INVALID_CREDENTIALS("invalid credentials"),
    ERROR_FETCHING_USER_INFORMATION("Error fetching user information"),
    PASSWORD_NOT_ACCEPTED("password not accepted"),
    ACCOUNT_ALREADY_ENABLED("Account has been enabled"),
    ACCOUNT_ALREADY_DISABLED("Account is not currently enabled"),
    INVALID_EMAIL_OR_PASSWORD("Invalid email or password");
    private final String message;

    IdentityMessages(String message){
        this.message = message;
    }

}
