package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.math.*;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
//    private final CohortOutputPort cohortOutputPort;
    private final CohortRepository cohortRepository;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;

    @Override
    public List<Program> findProgramByName(String programName) throws MeedlException {
        validateDataElement(programName);
        List<ProgramEntity> programEntities = programRepository.findByNameContainingIgnoreCase(programName.trim());
        log.info("Program entities: {}", programEntities);
        if (programEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return programEntities.stream().map(programMapper::toProgram).toList();
    }

    @Override
    public Program saveProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        program.validate();

        log.info("Program at persistence layer: ========>{}", program);
        OrganizationIdentity organizationIdentity = findCreatorOrganization(program.getCreatedBy());
        log.info("The organization identity found when saving program is: {}", organizationIdentity);
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.findServiceOfferingById(organizationIdentity.getId());
        ProgramPersistenceAdapter.validateServiceOfferings(serviceOfferings);

        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);

        ProgramEntity programEntity = programMapper.toProgramEntity(program);
        programEntity.setOrganizationEntity(organizationEntity);
        programEntity = programRepository.save(programEntity);
        updateOrganization(program, organizationEntity);
        Program retrivedProgram  = programMapper.toProgram(programEntity);
        retrivedProgram.setOrganizationId(organizationEntity.getId());
        return retrivedProgram;
    }

    private void updateOrganization(Program program, OrganizationEntity organizationEntity) {
        if (StringUtils.isEmpty(program.getId())) {
            organizationEntity.setNumberOfPrograms(organizationEntity.getNumberOfPrograms() + BigInteger.ONE.intValue());
            organizationEntityRepository.save(organizationEntity);
        }
    }

    @Override
    public  OrganizationIdentity findCreatorOrganization(String meedlUserId) throws MeedlException {
        MeedlValidator.validateUUID(meedlUserId);
        log.info("Validating the created by: {}",meedlUserId);
        OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByCreatedBy(meedlUserId);
        if (ObjectUtils.isEmpty(employeeIdentity)) {
            throw new EducationException(MeedlMessages.INVALID_CREATED_BY.getMessage());
        }
        return organizationIdentityOutputPort.findById(employeeIdentity.getOrganization());
    }

    @Override
    public boolean programExists(String programName) throws MeedlException {
        validateDataElement(programName);
        return programRepository.existsByName(programName);
    }

    @Override
    public void deleteProgram(String programId) throws MeedlException {
        MeedlValidator.validateUUID(programId);
        ProgramEntity program = programRepository.findById(programId).
                orElseThrow(()-> new ResourceNotFoundException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage()));
        List<CohortEntity> cohortEntities = cohortRepository.findAllByProgramId(program.getId());
        if (CollectionUtils.isNotEmpty(cohortEntities)) {
            cohortRepository.deleteAll(cohortEntities);
        }
        programRepository.delete(program);
    }

    @Override
    public Program findProgramById(String programId) throws MeedlException {
        MeedlValidator.validateDataElement(programId);
        MeedlValidator.validateUUID(programId);
        programId = programId.trim();
        ProgramEntity programEntity = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        Program program = programMapper.toProgram(programEntity);
        program.setOrganizationId(programEntity.getOrganizationEntity().getId());
        return program;
    }

    @Override
    public Page<Program> findAllPrograms(String meedlUserId, int pageSize, int pageNumber) throws MeedlException {
        OrganizationIdentity foundOrganizationIdentity = findCreatorOrganization(meedlUserId);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("createdAt")));
        Page<ProgramEntity> programEntities = programRepository.findAllByOrganizationEntityId(foundOrganizationIdentity.getId(), pageRequest);
        return programEntities.map(programMapper::toProgram);
    }
    private static void validateServiceOfferings(List<ServiceOffering> serviceOfferings) throws EducationException {
        log.info("Validating service offerings: {}", serviceOfferings);
        if(CollectionUtils.isEmpty(serviceOfferings) ||
                !serviceOfferings.stream().map(ServiceOffering::getName).toList().contains(ServiceOfferingType.TRAINING.name())) {
            throw new EducationException(ProgramMessages.INVALID_SERVICE_OFFERING.getMessage());
        }
    }
}
