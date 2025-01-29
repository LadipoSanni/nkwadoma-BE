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
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_ALREADY_EXISTS;
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
    private final CohortRepository cohortRepository;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private final LoanBreakdownRepository loanBreakdownRepository;
    private final ProgramCohortRepository programCohortRepository;

    @Override
    public List<Program> findProgramByName(String programName, String organizationId) throws MeedlException {
        MeedlValidator.validateDataElement(programName, ProgramMessages.PROGRAM_NAME_REQUIRED.getMessage());
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        List<ProgramEntity> programEntities = programRepository.
                findByNameContainingIgnoreCaseAndOrganizationIdentityId(programName.trim(), organizationId);
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
        log.info("Saving program with name: {}", program.getName());
        ProgramPersistenceAdapter.validateServiceOfferings(program.getOrganizationIdentity().getServiceOfferings());
        log.info("Program at persistence layer: ========>{}", program);
        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(program.getOrganizationIdentity());
        ProgramEntity programEntity = programMapper.toProgramEntity(program);
        programEntity.setOrganizationIdentity(organizationEntity);
        programEntity.setProgramStatus(ActivationStatus.ACTIVE);
        programEntity = programRepository.save(programEntity);
        updateOrganization(program, organizationEntity);
        Program retrivedProgram  = programMapper.toProgram(programEntity);
        retrivedProgram.setOrganizationId(organizationEntity.getId());
        return retrivedProgram;
    }

    private void updateOrganization(Program program, OrganizationEntity organizationEntity) {
        if (StringUtils.isEmpty(program.getId())) {
            organizationEntity.setNumberOfPrograms(organizationEntity.getNumberOfPrograms() + BigInteger.ONE.intValue());
            log.info("Updating total number of programs in organization to {}",organizationEntity.getNumberOfPrograms());
            organizationEntityRepository.save(organizationEntity);
        }
    }
    @Override
    public  OrganizationIdentity findCreatorOrganization(String meedlUserId) throws MeedlException {
        MeedlValidator.validateUUID(meedlUserId, MeedlMessages.INVALID_CREATED_BY_ID.getMessage());
        log.info("Validating the created by: {}",meedlUserId);
        MeedlValidator.validateUUID(meedlUserId);
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
    public boolean programExistsInOrganization(Program program) throws MeedlException {
        MeedlValidator.validateDataElement(program.getName(),ProgramMessages.PROGRAM_NAME_REQUIRED.getMessage());
        log.error("Checking if this program name : {}, exists in organization: {}", program.getName(), program.getOrganizationId());
        return programRepository.existsByNameAndOrganizationIdentity_Id(program.getName(), program.getOrganizationId() );
    }

    @Override
    @Transactional
    public void deleteProgram(String programId) throws MeedlException {
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        ProgramEntity program = programRepository.findById(programId).
                orElseThrow(()-> new ResourceNotFoundException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage()));
        List<CohortEntity> cohortEntities = cohortRepository.findAllByProgramId(program.getId());

        if (CollectionUtils.isNotEmpty(cohortEntities)) {
            for (CohortEntity cohortEntity : cohortEntities) {
                if (cohortEntity.getNumberOfLoanees() > 0) {
                    throw new EducationException("Program with loanee cannot be deleted");
                }
                else {
                    programCohortRepository.deleteAllByCohort(cohortEntity);
                    loanBreakdownRepository.deleteAllByCohort(cohortEntity);
                    cohortRepository.deleteById(cohortEntity.getId());
                }
            }
        }
        programRepository.delete(program);
    }

    @Override
    public Program findProgramById(String programId) throws MeedlException {
        MeedlValidator.validateDataElement(programId);
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        programId = programId.trim();
        ProgramEntity programEntity = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        Program program = programMapper.toProgram(programEntity);
        program.setOrganizationId(programEntity.getOrganizationIdentity().getId());
        program.setOrganizationIdentity(
                organizationIdentityMapper.toOrganizationIdentity(programEntity.getOrganizationIdentity()));
        log.info("Program found id: {}, for organization with id : {} :: {}", program.getId(), program.getOrganizationIdentity().getId(), program.getOrganizationIdentity().getServiceOfferings());
        return program;
    }

    @Override
    public Page<Program> findAllPrograms(String meedlUserId, int pageSize, int pageNumber) throws MeedlException {
        OrganizationIdentity foundOrganizationIdentity = findCreatorOrganization(meedlUserId);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("createdAt")));
        Page<ProgramEntity> programEntities = programRepository.findAllByOrganizationIdentityId(foundOrganizationIdentity.getId(), pageRequest);
        return programEntities.map(programMapper::toProgram);
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
