package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;

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
        MeedlValidator.validateDataElement(program.getOrganizationId());
        String organizationId = program.getOrganizationId().trim();
        return programOutputPort.findAllPrograms(organizationId, program.getPageSize(), program.getPageNumber());
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

}
