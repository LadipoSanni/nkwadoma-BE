package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum UserMessages {

    INVALID_EMAIL("Email is invalid"),
    INVALID_USER_ID("Please provide a valid user identification."),
    INVALID_FIRST_NAME("First name is invalid"),
    NEW_PASSWORD_AND_CURRENT_PASSWORD_CANNOT_BE_SAME("New password and current password cannot be the same"),
    USER_IDENTITY_CANNOT_BE_EMPTY("User identity cannot be empty"),
    USER_IDENTITY_MUST_NOT_BE_EMPTY("User identity must not be empty"),
    BVN_CANNOT_BE_EMPTY("Bvn cannot be empty");
    private final String message;

    UserMessages(String message) {
        this.message = message;
    }
}
