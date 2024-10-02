package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.ProgramException;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;

public interface AddProgramUseCase {
    Program addProgram(Program program) throws MiddlException;
}
