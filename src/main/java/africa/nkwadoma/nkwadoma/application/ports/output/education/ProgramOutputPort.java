package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;

import java.util.*;

public interface ProgramOutputPort {
    Optional<Program> findProgramByName(String programName) throws ResourceNotFoundException;
    Program saveProgram(Program program) throws MiddlException;
}
