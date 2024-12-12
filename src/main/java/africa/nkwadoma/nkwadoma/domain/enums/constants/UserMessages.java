package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum UserMessages {

    INVALID_EMAIL("Email is invalid"),
    INVALID_USER_ID("Please provide a valid user identification."),
    INVALID_FIRST_NAME("First name is invalid");
    private final String message;

    UserMessages(String message) {
        this.message = message;
    }
}
