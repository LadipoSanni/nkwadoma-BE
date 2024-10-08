package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;

public interface ProgramOutputPort {
    Program saveProgram(Program program) throws ResourceNotFoundException, ResourceAlreadyExistsException;
}
