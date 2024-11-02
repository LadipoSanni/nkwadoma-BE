package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;

@Getter
public enum ProgramMessages {
    THIS_FIELD_IS_REQUIRED("This field is required"),
    PROGRAM_NOT_FOUND("Program not found"),
    PROGRAM_ALREADY_EXISTS("Program already exists"),
    WRONG_INDUSTRY("Organization's industry must be education"),
    COHORT_EXISTS("Program with cohort cannot be deleted"),
    INVALID_SERVICE_OFFERING("Service Offering must be TRAINING");
    private final String message;
    ProgramMessages(String message) {
        this.message = message;
    }
}
