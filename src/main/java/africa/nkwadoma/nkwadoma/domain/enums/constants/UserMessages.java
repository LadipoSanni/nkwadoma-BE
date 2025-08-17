package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum UserMessages {

    INVALID_EMAIL("Email is invalid"),
    NULL_ACTOR_USER_IDENTITY("User performing this action is unknown."),
    INVALID_USER_ID("Please provide a valid user identification."),
    INVALID_ROLE_ASSIGNER_ID("Invalid id for user assigning role"),
    INVALID_ROLE_ASSIGNEE_ID("Invalid id for user being assigned a new role"),
    INVALID_FIRST_NAME("User first name is required."),
    INVALID_LAST_NAME("User last name is required."),
    NEW_PASSWORD_AND_CURRENT_PASSWORD_CANNOT_BE_SAME("New password and current password cannot be the same"),
    USER_IDENTITY_CANNOT_BE_EMPTY("User identity cannot be empty"),
    USER_IDENTITY_MUST_NOT_BE_EMPTY("User identity must not be empty"),
    BVN_CANNOT_BE_EMPTY("Bvn cannot be empty"),
    REFRESH_TOKEN_CANNOT_BE_EMPTY("Refresh token cannot be empty"), INVALID_REFRESH_TOKEN("Invalid refresh token"),
    COOPERATION_MUST_NOT_BE_EMPTY("Cooperation cannot be empty"),
    USER_HAS_BEEN_DEACTIVATED("Account Deactivation"),
    DEACTIVATED_USER("deactivated-user"),
    USER_HAS_BEEN_REACTIVATED("Account Reactivation"),
    REACTIVATED_USER("reactivated-user");
    private final String message;

    UserMessages(String message) {
        this.message = message;
    }
}
