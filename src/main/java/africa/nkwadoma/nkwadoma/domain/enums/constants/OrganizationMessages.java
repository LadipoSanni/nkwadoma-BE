package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum OrganizationMessages {
    ORGANIZATION_RC_NUMBER_ALREADY_EXIST("Organization with this RC number already exists"),
    ORGANIZATION_NOT_FOUND("Organization not found"),
    INVALID_ORGANIZATION_ID("Please provide a valid organization identification."),
    ORGANIZATION_ALREADY_EXISTS("Organization already exists"),
    INVALID_INDUSTRY("Organization's industry must be BANKING or EDUCATION");

    private final String message;
    OrganizationMessages(String message) {
        this.message = message;
    }

}
