package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum OrganizationMessages {
    INVALID_ORGANIZATION_ID("Please provide a valid organization identification."),
    RC_NUMBER_IS_REQUIRED("Company's RC number is required"),
    INVALID_SERVICE_OFFERING_ID("Please provide a valid service offering identification."),
    INVALID_INDUSTRY("Organization's industry must be BANKING or EDUCATION"),
    INVALID_RC_NUMBER("Invalid Registration number i.e RC"),
    ORGANIZATION_MUST_NOT_BE_EMPTY("Organization identity must not be empty"),
    ORGANIZATION_STATUS_MUST_NOT_BE_EMPTY("Organization status must not be empty"),
    ORGANIZATION_TYPE_MUST_NOT_BE_EMPTY("Organization type must not be empty"),
    LOAN_METRICS_NOT_FOUND("No loan metrics found"),
    ORGANIZATION_NAME_IS_REQUIRED("Organization name is required"),
    ORGANIZATION_NOT_FOUND("Organization not found"),
    ORGANIZATION_ID_IS_REQUIRED("Organization ID is required"),
    ORGANIZATION_EMPLOYEE_IS_ACTIVE("Organization employee is active, Employee can't be Invited"),
    DECISION_CAN_EITHER_BE_APPROVED_OR_DECLINED("Decision can be approved or declined"),;

    private final String message;
    OrganizationMessages(String message) {
        this.message = message;
    }

}
