package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.*;

@Getter
public enum ProgramMessages {
    PROGRAM_NOT_FOUND("Program not found"),
    PROGRAM_CANNOT_BE_EMPTY("Program cannot be empty"),
    PROGRAM_NAME_REQUIRED("Program name is required"),
    INVALID_PROGRAM_ID("Please provide a valid program identification."),
    PROGRAM_ALREADY_EXISTS("A program with this name already exists"),
    WRONG_INDUSTRY("Organization's industry must be education"),
    COHORT_EXISTS("Program with cohort cannot be deleted"),
    INVALID_SERVICE_OFFERING("Service Offering must be TRAINING"),
    PROGRAM_DURATION_CANNOT_BE_NEGATIVE("Program duration cannot be negative"),
    PROGRAM_WITH_LOANEE_CANNOT_BE_EDITED("Program with loanees cannot be edited");
    private final String message;

    ProgramMessages(String message) {
        this.message = message;
    }
}
