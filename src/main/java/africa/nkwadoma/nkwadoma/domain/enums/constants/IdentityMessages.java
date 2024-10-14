package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum IdentityMessages{

    USER_NOT_FOUND("User not found!"),
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
    CREATE_PASSWORD_URL("/create-password?token="),
    PASSWORD_HAS_BEEN_CREATED("Password has been created Already"),
    PASSWORD_PATTERN("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,16}$"),
    INVALID_PASSWORD("Password validation failed"),
    INVALID_CREDENTIALS("invalid credentials"),
    ERROR_FETCHING_USER_INFORMATION("Error fetching user information"),
    PASSWORD_HISTORY_EMPTY("password history is empty"),
    PASSWORD_NOT_ACCEPTED("password not accepted"),
    ACCOUNT_ALREADY_ENABLED("Account has been enabled"),
    ACCOUNT_ALREADY_DISABLED("Account has been disabled");
    private final String message;

    IdentityMessages(String message){
        this.message = message;
    }

}
