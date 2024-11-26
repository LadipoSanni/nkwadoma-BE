package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramCohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
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

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages.*;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.USER_NOT_FOUND;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateDataElement;
import static africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator.validateUUID;


@Slf4j
@RequiredArgsConstructor
public class CohortPersistenceAdapter implements CohortOutputPort {


    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final ProgramCohortOutputPort programCohortOutputPort;
    private final LoanBreakdownRepository loanBreakdownRepository;


    @Override
    public Cohort viewCohortDetails(String userId, String programId, String cohortId) throws MeedlException {
        validateDataElement(userId);
        validateDataElement(programId);
        validateDataElement(cohortId);
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        if (userIdentity == null){
            throw new IdentityException(USER_NOT_FOUND.getMessage());
        }
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return getCohort(cohortId,programCohorts );
    }

    @Transactional
    @Override
    public void deleteCohort(String id) throws MeedlException {
        MeedlValidator.validateDataElement(id);
        CohortEntity cohortEntity = cohortRepository.findById(id).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        programCohortOutputPort.deleteAllByCohort(cohortEntity);
        loanBreakdownRepository.deleteAllByCohort(cohortEntity);
        cohortRepository.deleteById(id);
    }

    @Override
    public Cohort findCohort(String cohortId) throws CohortException {
        CohortEntity cohortEntity = cohortRepository.findById(cohortId).orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage()));
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Cohort save(Cohort cohort) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohort);
        cohort.validate();
        CohortEntity cohortEntity = cohortMapper.toCohortEntity(cohort);
        cohortEntity = cohortRepository.save(cohortEntity);
        return cohortMapper.toCohort(cohortEntity);
    }

    @Override
    public Cohort findCohortByName(String name) throws MeedlException {
        MeedlValidator.validateDataElement(name);
        CohortEntity cohortEntity = cohortRepository.findCohortByName(name);
        return cohortMapper.toCohort(cohortEntity);
    }

    private static Cohort getCohort(String cohortId, List<ProgramCohort> programCohorts) throws CohortException {
        return programCohorts.stream()
                .filter(eachCohort -> eachCohort.getCohort().getId().equals(cohortId))
                .findFirst()
                .orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }

    @Override
    public Cohort searchForCohortInAProgram(String name, String programId) throws MeedlException {
        List<ProgramCohort> programCohorts = programCohortOutputPort.findAllByProgramId(programId);
        return programCohorts.stream()
                .filter(eachProgramCohort -> eachProgramCohort.getCohort().getName().equalsIgnoreCase(name.trim()))
                .findFirst().orElseThrow(() -> new CohortException(COHORT_DOES_NOT_EXIST.getMessage())).getCohort();
    }

    @Override
    public Page<Cohort> findAllCohortInAProgram(String programId,int pageSize,int pageNumber) throws MeedlException {
        validateUUID(programId);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.asc("cohortStatus")));
        Page<CohortEntity> cohortEntities = cohortRepository.findAllByProgramId(programId, pageRequest);
        return cohortEntities.map(cohortMapper::toCohort);
    }

}

