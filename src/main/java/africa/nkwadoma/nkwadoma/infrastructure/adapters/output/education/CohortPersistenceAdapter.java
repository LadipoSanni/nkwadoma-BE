package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.CohortException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanBreakdownRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {
    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanBreakdownRepository loanBreakdownRepository;


    @Override
    public Cohort viewCohortDetails(String userId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity == null){
            throw new IdentityException(USER_NOT_FOUND.getMessage());
        }
        return findCohort(cohortId);
    }

    @Transactional
    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        CohortEntity cohortEntity = cohortRepository.findById(id).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        programCohortOutputPort.deleteAllByCohort(cohortEntity);
        loanBreakdownRepository.deleteAllByCohort(cohortEntity);
        cohortRepository.deleteById(id);
    }

    @Override
    public Cohort findCohort(String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, INVALID_COHORT_ID.getMessage());
        CohortEntity cohortEntity = cohortRepository.findById(cohortId).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Cohort save(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort, INPUT_CANNOT_BE_NULL.getMessage());
        cohort.validate();
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        cohortEntity = cohortRepository.save(cohortEntity);
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public List<Cohort> findCohortByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, CohortMessages.COHORT_NAME_REQUIRED.getMessage());
        List<CohortEntity> cohortEntities = cohortRepository.findByNameContainingIgnoreCase(name);
        if (cohortEntities.isEmpty()){
            return new ArrayList<>();
        }
        return cohortEntities.stream().map(cohortMapper::toCohort).toList();
    }

    @Override
    public Page<Cohort> findAllCohortByOrganizationId(String organizationId, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, "Please provide a valid organization identification");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("cohortStatus")));
        Page<CohortEntity> cohortEntities = cohortRepository.findAllByOrganizationId(organizationId,pageRequest);
        return cohortEntities.map(cohortMapper::toCohort);
    }

    @Override
    public List<Cohort> searchForCohortInAProgram(String name,String programId) throws MeedlException {
        MeedlValidator.validateDataElement(name, CohortMessages.COHORT_NAME_REQUIRED.getMessage());
        MeedlValidator.validateUUID(programId , "Please provide a valid program identification");
        List<CohortEntity> cohortEntities = cohortRepository.findByProgramIdAndNameContainingIgnoreCase(programId,name);
        if (cohortEntities.isEmpty()){
            return new ArrayList<>();
        }
        return cohortEntities.stream().map(cohortMapper::toCohort).toList();
    }

    @Override
    public Cohort checkIfCohortExistWithName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name, CohortMessages.COHORT_NAME_REQUIRED.getMessage());
        CohortEntity cohortEntity = cohortRepository.findByName(name);
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public List<Cohort> searchCohortInOrganization(String organizationId, String name) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, "Provide a valid organization identification");
        MeedlValidator.validateDataElement(name, COHORT_NAME_REQUIRED.getMessage());
        List<CohortEntity> cohortEntities =
                cohortRepository.findByOrganizationIdAndNameContainingIgnoreCase(organizationId,name);
        if (cohortEntities.isEmpty()){
            return new ArrayList<>();
        }
        return cohortEntities.stream().map(cohortMapper::toCohort).toList();
    }

    private static Cohort getCohort(String cohortId, List<ProgramCohort> programCohorts) throws CohortException {
        return programCohorts.stream()
                .filter(eachCohort -> eachCohort.getCohort().getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }


    @Override
    public Page<Cohort> findAllCohortInAProgram(String programId,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(programId, "Provide a valid program identification");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("cohortStatus")));
        Page<CohortEntity> cohortEntities = cohortRepository.findAllByProgramId(programId, pageRequest);
        return cohortEntities.map(cohortMapper::toCohort);
    }

}

