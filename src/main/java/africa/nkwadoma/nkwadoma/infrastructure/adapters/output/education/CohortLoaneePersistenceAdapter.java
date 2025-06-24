package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


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
}
