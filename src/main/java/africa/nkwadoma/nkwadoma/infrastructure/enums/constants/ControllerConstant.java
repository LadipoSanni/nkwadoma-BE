package africa.nkwadoma.nkwadoma.infrastructure.enums.constants;

import lombok.*;

@Getter
public enum ControllerConstant {
    RESPONSE_IS_SUCCESSFUL("Response is successful"),
    LOGOUT_SUCCESSFUL("Logout successful"),
    PASSWORD_CREATED_SUCCESSFULLY("Password has been created successfully"),;
    private final String message;

    ControllerConstant(String message) {
        this.message = message;
    }
}
