package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;
    private final ProgramMapper programMapper;

    @Override
    public Program createProgram(Program program) throws MeedlException {
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
        MeedlValidator.validateUUID(program.getId());
        Program foundProgram = programOutputPort.findProgramById(program.getId());
        if (ObjectUtils.isNotEmpty(foundProgram)) {
            foundProgram = programMapper.updateProgram(program, foundProgram);
            log.info("Program at service layer: ========>{}", foundProgram);
        }
        return programOutputPort.saveProgram(foundProgram);
    }

    @Override
    public Program viewProgramByName(Program program) throws MeedlException {
        MeedlValidator.validateDataElement(program.getName());
        String programName = program.getName().trim();
        return programOutputPort.findProgramByName(programName);
    }

    @Override
    public void deleteProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        MeedlValidator.validateDataElement(program.getId());
        String programId = program.getId().trim();
        MeedlValidator.validateUUID(programId);
        Program foundProgram = programOutputPort.findProgramById(programId);
        programOutputPort.deleteProgram(foundProgram.getId());
    }

    @Override
    public Program viewProgramById(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        MeedlValidator.validateDataElement(program.getId());
        String programId = program.getId().trim();
        MeedlValidator.validateUUID(programId);
        return programOutputPort.findProgramById(programId);
    }

}
