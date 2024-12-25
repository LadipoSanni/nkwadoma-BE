package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum OrganizationMessages {
    INVALID_ORGANIZATION_ID("Please provide a valid organization identification."),
    RC_NUMBER_IS_REQUIRED("Company's RC number is required"),
    INVALID_SERVICE_OFFERING_ID("Please provide a valid service offering identification."),
    INVALID_INDUSTRY("Organization's industry must be BANKING or EDUCATION"),
    INVALID_RC_NUMBER("Invalid Registration number"),
    ORGANIZATIOM_MUST_NOT_BE_EMPTY("Organization identity must not be empty"),
    LOAN_METRICS_NOT_FOUND("No loan metrics found");

    private final String message;
    OrganizationMessages(String message) {
        this.message = message;
    }

}
