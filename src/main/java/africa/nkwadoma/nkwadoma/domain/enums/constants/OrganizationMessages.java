package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum OrganizationMessages {
    ORGANIZATION_RC_NUMBER_ALREADY_EXIST("Organization with this RC number already exists"),
    ORGANIZATION_NOT_FOUND("Organization not found"),
    INVALID_ORGANIZATION_ID("Please provide a valid organization identification."),
    INVALID_RC_NUMBER("Company's RC number is required"),
    INVALID_SERVICE_OFFERING_ID("Please provide a valid service offering identification."),
    ORGANIZATION_ALREADY_EXISTS("Organization already exists"),
    INVALID_INDUSTRY("Organization's industry must be BANKING or EDUCATION"),
    ORGANIZATIOM_MUST_NOT_BE_EMPTY("Organization must not be empty");

    private final String message;
    OrganizationMessages(String message) {
        this.message = message;
    }

}
