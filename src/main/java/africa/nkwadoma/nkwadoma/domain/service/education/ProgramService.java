package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.ProgramValidator.validateInput;

@Service
@RequiredArgsConstructor
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;

    @Override
    public Program createProgram(Program program) throws MiddlException {
        validateInput(program);
        Optional<Program> foundProgram = programOutputPort.findProgramByName(program.getName());
        if (foundProgram.isPresent()) {
            throw new ResourceAlreadyExistsException(PROGRAM_ALREADY_EXISTS.getMessage());
        }
        else foundProgram = Optional.of(program);
        return programOutputPort.saveProgram(foundProgram.get());
    }

}
