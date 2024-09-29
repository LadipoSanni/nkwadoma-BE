package africa.nkwadoma.nkwadoma.domain.constants;

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
    USER_IDENTITY_ALREADY_EXISTS("UserIdentity already exists"),
    ENCODING_VALUE("utf-8"),
    CONTEXT_TOKEN("token"),
    CONTEXT_FIRST_NAME("firstName"),
    CONTEXT_CURRENT_YEAR("currentYear"),
    CREATE_PASSWORD_URL("/create-password?token=");
    private final String message;

    IdentityMessages(String message){
        this.message = message;
    }

}
