package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;

public interface AddProgramUseCase {
    Program createProgram(Program program) throws MeedlException;
}
