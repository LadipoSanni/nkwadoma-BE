package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.ProgramMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.OrganizationIdentityMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.math.*;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages.PROGRAM_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateUUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProgramPersistenceAdapter implements ProgramOutputPort {
    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final OrganizationIdentityMapper organizationIdentityMapper;
    private final OrganizationEntityRepository organizationEntityRepository;
    private final OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    private final CohortRepository cohortRepository;

    @Override
    public Program findProgramByName(String programName) throws MeedlException {
        validateDataElement(programName);
        programName = programName.trim();
        log.info("Program being searched for by name is {}", programName);
        ProgramEntity programEntity = programRepository.findByName(programName).
                orElseThrow(()-> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        return programMapper.toProgram(programEntity);
    }

    @Override
    public Program saveProgram(Program program) throws MeedlException {
        MeedlValidator.validateObjectInstance(program);
        program.validate();
        validateCreatedBy(program);

        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(program.getOrganizationId());
        log.info("The organization identity found when saving program is: {}", organizationIdentity);
        List<ServiceOffering> serviceOfferings = organizationIdentityOutputPort.findServiceOfferingById(organizationIdentity.getId());
        ProgramPersistenceAdapter.validateServiceOfferings(serviceOfferings);

        OrganizationEntity organizationEntity = organizationIdentityMapper.toOrganizationEntity(organizationIdentity);

        ProgramEntity programEntity = programMapper.toProgramEntity(program);
        programEntity.setOrganizationEntity(organizationEntity);
        programEntity = programRepository.save(programEntity);

        organizationEntity.setNumberOfPrograms(organizationEntity.getNumberOfPrograms() + BigInteger.ONE.intValue());
        organizationEntityRepository.save(organizationEntity);
        return programMapper.toProgram(programEntity);
    }

    private void validateCreatedBy(Program program) throws MeedlException {
        log.info("Validating the created by: {}",program.getCreatedBy());
        OrganizationEmployeeIdentity employeeIdentity = employeeIdentityOutputPort.findByCreatedBy(program.getCreatedBy());
    }


    @Override
    public boolean programExists(String programName) throws MeedlException {
        validateDataElement(programName);
        return programRepository.existsByName(programName);
    }

    @Override
    public void deleteProgram(String programId) throws MeedlException {
        MeedlValidator.validateDataElement(programId);
        programId = programId.trim();
        MeedlValidator.validateUUID(programId);
        Optional<CohortEntity> cohortEntity = cohortRepository.findByProgramId(programId);
        if (cohortEntity.isPresent()) {
            throw new EducationException(ProgramMessages.COHORT_EXISTS.getMessage());
        }
        ProgramEntity program = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(ProgramMessages.PROGRAM_NOT_FOUND.getMessage()));
        programRepository.delete(program);
    }

    @Override
    public Program findProgramById(String programId) throws MeedlException {
        MeedlValidator.validateDataElement(programId);
        MeedlValidator.validateUUID(programId);
        programId = programId.trim();
        ProgramEntity programEntity = programRepository.findById(programId).
                orElseThrow(() -> new ResourceNotFoundException(PROGRAM_NOT_FOUND.getMessage()));
        return programMapper.toProgram(programEntity);
    }

    @Override
    public Page<Program> findAllPrograms(String organizationId, int pageSize, int pageNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<ProgramEntity> programEntities = programRepository.findAllByOrganizationEntityId(organizationId, pageRequest);
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
