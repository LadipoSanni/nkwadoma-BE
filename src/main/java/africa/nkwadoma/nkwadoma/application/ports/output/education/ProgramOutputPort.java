package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;

import java.util.*;

public interface ProgramOutputPort {
    Program findProgramByName(String programName) throws ResourceNotFoundException;
    Program saveProgram(Program program) throws MeedlException;
    boolean programExists(String programName) throws MeedlException;

    void deleteProgram(String programId);

    Program findProgramById(String programId) throws ResourceNotFoundException;
}
