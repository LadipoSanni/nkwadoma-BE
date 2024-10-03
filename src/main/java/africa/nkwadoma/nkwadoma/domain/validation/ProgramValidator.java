package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.ProgramException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import org.apache.commons.lang3.StringUtils;

public class ProgramValidator {
    public static void validateInput(Program program) throws ProgramException {
        if (StringUtils.isEmpty(program.getName()) ||
                program.getDurationType() == null ||
                StringUtils.isEmpty(program.getOrganizationId())
        ){
            throw new ProgramException(ProgramMessages.THIS_FIELD_IS_REQUIRED.getMessage());
        }
    }
}
