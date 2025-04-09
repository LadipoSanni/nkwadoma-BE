package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.input.education.AddProgramUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService implements AddProgramUseCase {
    private final ProgramOutputPort programOutputPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private final ProgramMapper programMapper;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;

    @Override
    public Program createProgram(Program program) throws MeedlException {
        log.info("Creating program {}", program);
        program.validate();
        OrganizationIdentity organizationIdentity = findProgramOrganization(program);
        program.setOrganizationIdentity(organizationIdentity);
        checkIfProgramExistByNameInOrganization(program);
        return programOutputPort.saveProgram(program);
    }
    @Override
    public Program updateProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        Program foundProgram = programOutputPort.findProgramById(program.getId());
        if (ObjectUtils.isNotEmpty(foundProgram)) {
            log.info("Program at service layer update program: ========>{}", foundProgram);
            foundProgram = programMapper.updateProgram(program, foundProgram);
            OrganizationIdentity organizationIdentity = findProgramOrganization(program);
            program.setOrganizationIdentity(organizationIdentity);
//            checkIfProgramExistByNameInOrganization(foundProgram);
            List<Program> programs =
                    programOutputPort.findProgramByName(program.getName(),foundProgram.getOrganizationId());
            for (Program p : programs) {
                if (!p.getId().equals(program.getId())){
                  throw new MeedlException("Program name exist");
                }
            }
        }
        return programOutputPort.saveProgram(foundProgram);
    }
    private OrganizationIdentity findProgramOrganization(Program program) throws MeedlException {
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(program.getCreatedBy());
        log.info("The organization identity found when saving program is: {}", organizationIdentity);
        program.setOrganizationId(organizationIdentity.getId());
        return organizationIdentity;
    }
    private void checkIfProgramExistByNameInOrganization(Program program) throws MeedlException {
        boolean programExists = programOutputPort.programExistsInOrganization(program);
        log.info("Program exists {}. name {}, organization {}", programExists, program.getName(), program.getOrganizationId());
        if (programExists) {
            log.error("Program with name {} already exists in organization with id :{}", program.getName(), program.getOrganizationId());
            throw new ResourceAlreadyExistsException(PROGRAM_ALREADY_EXISTS.getMessage());
        }
        log.info("Program with name {} does not exists in organization with id :{}, therefore program can be created/updated.", program.getName(), program.getOrganizationId());
    }
    @Override
    public Page<Program> viewAllPrograms(Program program) throws MeedlException {
        return programOutputPort.findAllPrograms(program.getCreatedBy(), program.getPageSize(), program.getPageNumber());
    }

    @Override
    public List<Program> viewProgramByName(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        program.validateViewProgramByNameInput();
        return getPrograms(program);
    }

    private List<Program> getPrograms(Program program) throws MeedlException {
        UserIdentity foundCreator = userIdentityOutputPort.findById(program.getCreatedBy());
        log.info("Found User identity: {}", foundCreator);
        if (ObjectUtils.isNotEmpty(foundCreator) && foundCreator.getRole().equals(IdentityRole.ORGANIZATION_ADMIN)) {
            OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByCreatedBy(foundCreator.getId());
            log.info("Found Organization Employee: {}", employeeIdentity);
            return programOutputPort.findProgramByName(program.getName().trim(), employeeIdentity.getOrganization());
        }
        return programOutputPort.findProgramByName(program.getName().trim());
    }

    @Override
    public void deleteProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        Program foundProgram = programOutputPort.findProgramById(program.getId());
        programOutputPort.deleteProgram(foundProgram.getId());
        OrganizationIdentity  organizationIdentity =
                organizationIdentityOutputPort.findById(foundProgram.getOrganizationId());
        organizationIdentity.setNumberOfPrograms(organizationIdentity.getNumberOfPrograms() - 1);
        organizationIdentityOutputPort.save(organizationIdentity);
    }

    @Override
    public Program viewProgramById(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(program.getId(), ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        return programOutputPort.findProgramById(program.getId());
    }

}
