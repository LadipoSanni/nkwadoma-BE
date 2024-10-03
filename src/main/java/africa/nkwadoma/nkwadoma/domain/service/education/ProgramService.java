package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.ProgramException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ResourceNotFoundException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.validation.ProgramValidator.validateInput;

@Service
@RequiredArgsConstructor
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;

    @Override
    public Program createProgram(Program program) throws ProgramException, ResourceNotFoundException {
        validateInput(program);
        return programOutputPort.saveProgram(program);
    }
}
