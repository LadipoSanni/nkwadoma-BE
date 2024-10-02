package africa.nkwadoma.nkwadoma.domain.enums.constants;

import lombok.Getter;
import org.apache.commons.validator.Field;

@Getter
public enum ProgramMessages {
    THIS_FIELD_IS_REQUIRED("This field is required"),
    PROGRAM_NOT_FOUND("Program not found"),
    PROGRAM_ALREADY_EXISTS("Program already exists");
    private final String message;
    ProgramMessages(String message) {
        this.message = message;
    }
}
