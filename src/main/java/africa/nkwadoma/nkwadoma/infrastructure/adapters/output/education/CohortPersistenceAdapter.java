package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {
    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanBreakdownRepository loanBreakdownRepository;


    @Override
    public Cohort findCohortById(String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, INVALID_COHORT_ID.getMessage());
        CohortEntity cohortEntity = cohortRepository.findById(cohortId).orElseThrow(() -> new EducationException(COHORT_DOES_NOT_EXIST.getMessage()));
        log.info("cohort entity {}", cohortEntity);
        return cohortMapper.toCohort(cohortEntity);
    }

    @Transactional
    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        CohortEntity cohortEntity = cohortRepository.findById(id).orElseThrow(() -> new EducationException(COHORT_DOES_NOT_EXIST.getMessage()));
        programCohortOutputPort.deleteAllByCohort(cohortEntity);
        loanBreakdownRepository.deleteAllByCohort(cohortEntity);
        cohortRepository.deleteById(id);
    }

    @Override
    public Cohort save(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, INPUT_CANNOT_BE_NULL.getMessage());
        cohort.validate();
        log.info("Number of loanees in cohort before map save {}", cohort.getNumberOfLoanees());
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        cohortEntity = cohortRepository.save(cohortEntity);
        log.info("Number of loanees in cohort after db save {}", cohortEntity.getNumberOfLoanees());

        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Page<Cohort> findCohortByNameAndOrganizationId(Cohort cohort) throws MeedlException {
        MeedlValidator.validateUUID(cohort.getOrganizationId(), OrganizationMessages.ORGANIZATION_ID_IS_REQUIRED.getMessage());
        Pageable pageRequest = PageRequest.of(cohort.getPageNumber(), cohort.getPageSize(),
                Sort.by(Sort.Order.desc("createdAt")));
        Page<CohortProjection> cohortEntities = cohortRepository.findByNameContainingIgnoreCaseAndOrganizationId(cohort.getName(),
                cohort.getCohortStatus(),cohort.getOrganizationId(),cohort.getCohortType(),pageRequest);
        if (cohortEntities.isEmpty()){
            return Page.empty();
        }
        return cohortEntities.map(cohortMapper::mapFromProjectionToCohort);
    }

    @Override
    public Page<Cohort> findAllCohortByOrganizationId(String organizationId,Cohort cohort) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, "Please provide a valid organization identification");
        Pageable pageRequest = PageRequest.of(cohort.getPageNumber(), cohort.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        Page<CohortProjection> cohortEntities = cohortRepository.findAllByOrganizationIdAndCohortStatus(organizationId,
                pageRequest,cohort.getCohortStatus(),cohort.getCohortType());
        return cohortEntities.map(cohortMapper::mapFromProjectionToCohort);
    }

    @Override
    public Page<Cohort> searchForCohortInAProgram(String name,String programId,int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(programId , "Please provide a valid program identification");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("createdAt")));
        Page<CohortEntity> cohortEntities = cohortRepository.findByProgramIdAndNameContainingIgnoreCase(programId,name,pageRequest);
        if (cohortEntities.isEmpty()){
            return Page.empty();
        }
        return cohortEntities.map(cohortMapper::toCohort);
    }

    @Override
    public Cohort checkIfCohortExistWithName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, CohortMessages.COHORT_NAME_REQUIRED.getMessage());
        CohortEntity cohortEntity = cohortRepository.findByName(name);
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Page<Cohort> searchCohortInOrganization(String organizationId, String name,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, "Provide a valid organization identification");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdAt")));
        Page<CohortEntity> cohortEntities =
                cohortRepository.findByOrganizationIdAndNameContainingIgnoreCase(organizationId,name,pageRequest);
        if (cohortEntities.isEmpty()){
            return Page.empty();
        }
        return cohortEntities.map(cohortMapper::toCohort);
    }

    @Transactional
    @Override
    public int deleteAllCohortAssociateWithProgram(String id) throws MeedlException {
        MeedlValidator.validateUUID(id , ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        return cohortRepository.deleteAllCohortAssociateWithProgramIdAndGetCount(id);
    }

    private static Cohort getCohort(String cohortId, List<ProgramCohort> programCohorts) throws EducationException {
        return programCohorts.stream()
                .filter(eachCohort -> eachCohort.getCohort().getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new EducationException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }


    @Override
    public Page<Cohort> findAllCohortInAProgram(Cohort cohort) throws MeedlException {
        MeedlValidator.validateUUID(cohort.getProgramId(), "Provide a valid program identification");
        Pageable pageRequest = PageRequest.of(cohort.getPageNumber(), cohort.getPageSize(), Sort.by(Sort.Order.desc("createdAt")));
        Page<CohortProjection> cohortEntities = cohortRepository.findAllByProgramIdAndCohortStatus(cohort.getProgramId(),cohort.getCohortStatus(), pageRequest);
        return cohortEntities.map(cohortMapper::mapFromProjectionToCohort);
    }

}

