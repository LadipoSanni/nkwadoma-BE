package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.ORGANIZATION_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.ProgramValidator.validateInput;

@Service
@RequiredArgsConstructor
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;

    @Override
    public Program createProgram(Program program) throws InvalidInputException, ResourceAlreadyExistsException, ResourceNotFoundException {
        validateInput(program);
        checkOrganizationExists(program);
        return programOutputPort.saveProgram(program);
    }

    private void checkOrganizationExists(Program program) throws ResourceNotFoundException {
        if (!organizationIdentityOutputPort.existsById(program.getOrganizationId())) {
            throw new ResourceNotFoundException(ORGANIZATION_NOT_FOUND.getMessage());
        }
    }
}
