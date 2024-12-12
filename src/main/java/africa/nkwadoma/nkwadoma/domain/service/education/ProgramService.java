package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;
    private final ProgramMapper programMapper;

    @Override
    public Program createProgram(Program program) throws MeedlException {
        log.info("Creating program {}", program);
        program.validate();
        boolean programExists = programOutputPort.programExists(program.getName());
        if (programExists) {
            throw new ResourceAlreadyExistsException(PROGRAM_ALREADY_EXISTS.getMessage());
        }
        return programOutputPort.saveProgram(program);
    }

    @Override
    public Page<Program> viewAllPrograms(Program program) throws MeedlException {
        return programOutputPort.findAllPrograms(program.getCreatedBy(), program.getPageSize(), program.getPageNumber());
    }

    @Override
    public Program updateProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        Program foundProgram = programOutputPort.findProgramById(program.getId());
        if (ObjectUtils.isNotEmpty(foundProgram)) {
            foundProgram = programMapper.updateProgram(program, foundProgram);
            log.info("Program at service layer: ========>{}", foundProgram);
        }
        return programOutputPort.saveProgram(foundProgram);
    }

    @Override
    public List<Program> viewProgramByName(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        MeedlValidator.validateDataElement(program.getName(), ProgramMessages.PROGRAM_NAME_REQUIRED.getMessage());
        return programOutputPort.findProgramByName(program.getName().trim());
    }

    @Override
    public void deleteProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        Program foundProgram = programOutputPort.findProgramById(program.getId());
        programOutputPort.deleteProgram(foundProgram.getId());
    }

    @Override
    public Program viewProgramById(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        return programOutputPort.findProgramById(program.getId());
    }

}
