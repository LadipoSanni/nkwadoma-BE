package africa.nkwadoma.nkwadoma.application.ports.input.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import org.springframework.data.domain.*;

public interface AddProgramUseCase {
    Program createProgram(Program program) throws MeedlException;

    Page<Program> viewAllPrograms(Program program) throws MeedlException;

    Program updateProgram(Program program) throws MeedlException;
    Page<Program> viewProgramByName(Program program) throws MeedlException;

    Program viewProgramById(Program program) throws MeedlException;

    void deleteProgram(Program program) throws MeedlException;
}
