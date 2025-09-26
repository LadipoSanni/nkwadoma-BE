package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.identity.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    @Lazy
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;


    @Override
    public Page<Program> findProgramByNameWithinOrganization(Program program, String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validatePageSize(program.getPageSize());
        MeedlValidator.validatePageNumber(program.getPageNumber());

        Pageable pageRequest = PageRequest.of(program.getPageNumber(), program.getPageSize(), Sort.by(Sort.Order.asc("createdAt")));

        Page<ProgramEntity> programEntities = programRepository.
                findByNameContainingIgnoreCaseAndOrganizationIdentityId(program.getName(), organizationId,pageRequest);
        log.info("Program entities: {}", programEntities);
        if (programEntities.isEmpty()) {
            return Page.empty();
        }
        return programEntities.map(programMapper::toProgram);
    }

    @Override
    public Page<Program> findProgramByName(String programName,int pageNumber, int pageSize) throws MeedlException {

        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("createdAt")));
        Page<ProgramEntity> programEntities = programRepository.findByNameContainingIgnoreCase(programName,pageRequest);
        log.info("Program entities found: {}", programEntities);
        if (programEntities.isEmpty()) {
            return Page.empty();
        }
        return programEntities.map(programMapper::toProgram);
    }

    @Override
    public Program saveProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program, ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
        program.validate();
        log.info("Saving program with name: {}", program.getName());
        log.info("Program at persistence layer: ========>{}", program);
        ProgramEntity programEntity = programMapper.toProgramEntity(program);
        programEntity = programRepository.save(programEntity);
        return programMapper.toProgram(programEntity);
    }

    @Override
    public  OrganizationIdentity findCreatorOrganization(String meedlUserId) throws MeedlException {
        MeedlValidator.validateUUID(meedlUserId, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        log.info("Validating the created by: {}",meedlUserId);
        MeedlValidator.validateUUID(meedlUserId, UserMessages.INVALID_USER_ID.getMessage());
        OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByCreatedBy(meedlUserId);
        if (ObjectUtils.isEmpty(employeeIdentity)) {
            log.error("Unable to find employee performing this action on the data base. {}", MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
            throw new EducationException(MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        }
        return organizationIdentityOutputPort.findById(employeeIdentity.getOrganization());
    }

    @Override
    public List<Program> findAllProgramsByOrganizationId(String organizationId) {
         List<ProgramEntity> programEntities = programRepository.findProgramEntitiesByOrganizationIdentityId(organizationId);
         return programEntities.stream().map(programMapper::toProgram).toList();
    }

    @Override
    public Page<Program> findAllProgramByOrganizationId(String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId,OrganizationMessages.ORGANIZATION_ID_IS_REQUIRED.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ProgramProjection> programEntities = programRepository.findAllByOrganizationIdentityId(organizationId, pageRequest);
        return programEntities.map(programMapper::mapFromProgramProjectionToProgram);
    }

    @Override
    public boolean checkIfLoaneeExistInProgram(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        return programRepository.checkIfLaoneeExistsByProgramId(id);
    }

    @Override
    public boolean programExistsInOrganization(Program program) throws MeedlException {
        MeedlValidator.validateDataElement(program.getName(),ProgramMessages.PROGRAM_NAME_REQUIRED.getMessage());
        log.error("Checking if this program name : {}, exists in organization: {}", program.getName(), program.getOrganizationId());
        return programRepository.existsByNameIgnoreCaseAndOrganizationIdentityId(program.getName(), program.getOrganizationId(), program.getId());
    }

    @Override
    @Transactional
    public void deleteProgram(String programId) throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        programRepository.deleteById(programId);
    }

    @Override
    public Program findProgramById(String programId) throws MeedlException {
        MeedlValidator.validateDataElement(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        programId = programId.trim();
        ProgramEntity programEntity = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        Program program = programMapper.toProgram(programEntity);
        program.setOrganizationId(programEntity.getOrganizationIdentity().getId());
//        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(programEntity.getOrganizationIdentity().getId());
//        program.setOrganizationIdentity(organizationIdentity);
        log.info("Program found id: {}, for organization with id : {} :: {} Rc number init {}", program.getId(), program.getOrganizationIdentity().getId(), program.getOrganizationIdentity().getServiceOfferings(), program.getOrganizationIdentity().getRcNumber());
        return program;
    }

    @Override
    public Page<Program> findAllPrograms(String meedlUserId, int pageSize, int pageNumber) throws MeedlException {
        OrganizationIdentity foundOrganizationIdentity = findCreatorOrganization(meedlUserId);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ProgramProjection> programEntities = programRepository.findAllByOrganizationIdentityId(foundOrganizationIdentity.getId(), pageRequest);
        log.info("proigram entites size {}",programEntities.getContent().size());
        return programEntities.map(programMapper::mapFromProgramProjectionToProgram);
    }
    private static void validateServiceOfferings(List<ServiceOffering> serviceOfferings) throws EducationException {
        log.info("Validating service offerings: {}", serviceOfferings);
        if (CollectionUtils.isEmpty(serviceOfferings)) {
            log.error("No service offerings found");
            throw new EducationException("No service offerings found");
        }
        if(!serviceOfferings.stream().map(ServiceOffering::getName).toList().contains(ServiceOfferingType.TRAINING.name())) {
            log.error("Service offering was not valid for saving program");
            throw new EducationException(ProgramMessages.INVALID_SERVICE_OFFERING.getMessage());
        }
    }
}
