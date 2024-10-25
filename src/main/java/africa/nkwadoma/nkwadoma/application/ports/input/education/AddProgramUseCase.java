package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import org.springframework.data.domain.*;

import java.util.*;

public interface AddProgramUseCase {
    Program createProgram(Program program) throws MeedlException;
    Page<Program> viewAllPrograms(Program program) throws MeedlException;

    Program viewProgramByName(Program program) throws MeedlException;

    void deleteProgram(Program program) throws MeedlException;
}
