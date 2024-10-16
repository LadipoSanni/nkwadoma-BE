package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;

import java.util.*;

public interface AddProgramUseCase {
    Program createProgram(Program program) throws MeedlException;
    List<Program> viewAllPrograms(Program program) throws MeedlException;
}
