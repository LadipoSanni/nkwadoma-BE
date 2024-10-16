package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;
import static africa.nkwadoma.nkwadoma.domain.validation.education.ProgramValidator.validateInput;

@Service
@RequiredArgsConstructor
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;

    @Override
    public Program createProgram(Program program) throws MeedlException {
        program.validate();
        boolean programExists = programOutputPort.programExists(program.getName());
        if (programExists) {
            throw new ResourceAlreadyExistsException(PROGRAM_ALREADY_EXISTS.getMessage());
        }
        return programOutputPort.saveProgram(program);
    }

    @Override
    public List<Program> viewAllPrograms(Program program) throws MeedlException {
//        if (StringUtils.isEmpty(program.getOrganizationId())) {
//            throw new MeedlException("Organization ID is required");
//        }
        return programOutputPort.findAllPrograms(program.getOrganizationId());
    }

}
