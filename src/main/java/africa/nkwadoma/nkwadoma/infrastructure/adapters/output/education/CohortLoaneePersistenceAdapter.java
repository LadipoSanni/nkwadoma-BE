package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component
public class CohortLoaneePersistenceAdapter implements CohortLoaneeOutputPort {

    private final CohortLoaneeMapper cohortLoaneeMapper;
    private final CohortLoaneeRepository cohortLoaneeRepository;

    @Override
    public CohortLoanee save(CohortLoanee cohortLoanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohortLoanee, CohortMessages.COHORT_LOANEE_CANNOT_BE_NULL.getMessage());
        cohortLoanee.validate();

        CohortLoaneeEntity cohortLoaneeEntity = cohortLoaneeMapper.toCohortLoaneeEntity(cohortLoanee);
        log.info("After mapping cohort loanee to entity = = : {}", cohortLoaneeEntity);
        cohortLoaneeEntity = cohortLoaneeRepository.save(cohortLoaneeEntity);
        log.info("After saving cohort loanee = = : {}", cohortLoaneeEntity);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }


    @Override
    public void delete(String id) throws MeedlException {
        log.info("Deleting cohort loanee = : {}", id);
        MeedlValidator.validateUUID(id,CohortMessages.INVALID_COHORT_LOANEE_ID.getMessage());
        cohortLoaneeRepository.deleteById(id);
    }

    @Override
    public CohortLoanee findCohortLoaneeByLoaneeIdAndCohortId(String loaneeId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());

        CohortLoaneeEntity cohortLoaneeEntity =
                cohortLoaneeRepository.findCohortLoaneeEntityByLoanee_IdAndCohort_Id(loaneeId,cohortId);
        log.info("After finding cohort loanee = : {}", cohortLoaneeEntity);

        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }

    @Override
    public CohortLoanee findCohortLoaneeByProgramIdAndLoaneeId(String programId, String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());

        CohortLoaneeEntity cohortLoaneeEntity =
                cohortLoaneeRepository.findCohortLoaneeEntityByCohort_ProgramIdAndLoanee_Id(programId,loaneeId);
        log.info("After finding cohort loanee by program ID  = : {}", cohortLoaneeEntity);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }

    @Override
    public List<CohortLoanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());

        List<CohortLoaneeEntity> cohortLoaneeEntities =
                cohortLoaneeRepository.findAllCohortLoaneeEntityBy_CohortIdAnd_ListOfLoaneeId(id,loaneeIds);

        log.info("found selected loanee = : {}", cohortLoaneeEntities);

        return cohortLoaneeEntities.stream().map(cohortLoaneeMapper::toCohortLoanee).collect(Collectors.toList());
    }

    @Override
    public boolean checkIfLoaneeHasBeenPreviouslyReferred(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Long count = cohortLoaneeRepository.countByLoaneeId(loaneeId);
        return count > 1;
    }

    @Override
    public Page<CohortLoanee> findAllLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getCohortId(), CohortMessages.INVALID_COHORT_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdAt")));

        Page<CohortLoaneeEntity> cohortLoanees = cohortLoaneeRepository.
                findAllByCohortId(loanee.getCohortId(),loanee.getLoaneeStatus(),loanee.getUploadedStatus(),pageRequest);
        log.info("found all cohort loanee = : {}", cohortLoanees);

        return cohortLoanees.map(cohortLoaneeMapper::toCohortLoanee);
    }
}
