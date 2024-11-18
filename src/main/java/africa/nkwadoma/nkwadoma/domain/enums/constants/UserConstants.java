package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.*;

@Getter
public enum UserConstants {

    INVALID_EMAIL("Email is invalid"),
    INVALID_FIRST_NAME("First name is invalid");
    private final String message;

    UserConstants(String message) {
        this.message = message;
    }
}
