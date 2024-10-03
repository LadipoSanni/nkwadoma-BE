package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ProgramException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.validation.ProgramValidator.validateInput;

@Service
@RequiredArgsConstructor
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;

    @Override
    public Program addProgram(Program program) throws ProgramException {
        validateInput(program);
        return programOutputPort.saveProgram(program);
    }
}
