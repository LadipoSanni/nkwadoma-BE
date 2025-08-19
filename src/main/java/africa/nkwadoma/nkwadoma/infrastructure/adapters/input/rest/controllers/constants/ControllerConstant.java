package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.controllers.constants;

import lombok.*;

@Getter
public enum ControllerConstant {
    RESPONSE_IS_SUCCESSFUL("Response is successful."),
    ROLE_ASSIGNED_SUCCESSFULLY("Response is successful."),
    RESPONSE_SUCCESSFUL_KYC("Kyc was done successfully."),
    INVESTED_SUCCESSFUL("Invested successful"),
    COLLEAGUE_INVITED("Colleague invited successfully"),
    LOGOUT_SUCCESSFUL("Logout successful"),
    PASSWORD_CREATED_SUCCESSFULLY("Password has been created successfully"),
    DELETED_SUCCESSFULLY("Deleted successfully"),
    UPDATED_SUCCESSFULLY("updated successfully"),
    PASSWORD_RESET_SUCCESSFUL("Password reset successful"),
    VIEW_EMPLOYEE_DETAILS_SUCCESSFULLY("Successfully retrieved financier investment details"),
    RETURNED_SUCCESSFULLY("Returned successfulLy");
    private final String message;

    ControllerConstant(String message) {
        this.message = message;
    }
}
