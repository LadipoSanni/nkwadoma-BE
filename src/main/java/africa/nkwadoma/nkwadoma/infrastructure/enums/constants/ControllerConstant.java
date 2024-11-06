package africa.nkwadoma.nkwadoma.infrastructure.enums.constants;

import lombok.*;

@Getter
public enum ControllerConstant {
    RESPONSE_IS_SUCCESSFUL("Response is successful"),
    COLLEAGUE_INVITED("Colleague invited successfully"),
    LOGOUT_SUCCESSFUL("Logout successful"),
    PASSWORD_CREATED_SUCCESSFULLY("Password has been created successfully"),
    DELETED_SUCCESSFULLY("Deleted successfully"),
    UPDATED_SUCCESSFULLY("updated successfully"),
    PASSWORD_RESET_SUCCESSFUL("Password reset successful");
    private final String message;

    ControllerConstant(String message) {
        this.message = message;
    }
}
