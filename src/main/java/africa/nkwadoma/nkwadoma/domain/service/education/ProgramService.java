package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

}
