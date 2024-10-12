package africa.nkwadoma.nkwadoma.domain.validation.education;

import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import org.apache.commons.lang3.StringUtils;

public class ProgramValidator {
    public static void validateInput(Program program) throws InvalidInputException {
        if (StringUtils.isEmpty(program.getName()) ||
                program.getDurationType() == null ||
                StringUtils.isEmpty(program.getOrganizationId())
        ){
            throw new InvalidInputException(ProgramMessages.THIS_FIELD_IS_REQUIRED.getMessage());
        }
    }
}
