package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoanDetailMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoanDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CohortLoanDetailPersistenceAdapter implements CohortLoanDetailOutputPort {

    private final CohortLoanDetailMapper cohortLoanDetailMapper;
    private final CohortLoanDetailRepository cohortLoanDetailRepository;

    @Override
    public CohortLoanDetail save(CohortLoanDetail cohortLoanDetail) throws MeedlException {
        log.info("-------> Cohort detail about to save: {}", cohortLoanDetail);
        MeedlValidator.validateObjectInstance(cohortLoanDetail, CohortMessages.COHORT_LOANEE_CANNOT_BE_NULL.getMessage());
        cohortLoanDetail.validate();
        CohortLoanDetailEntity cohortLoanDetailEntity = cohortLoanDetailMapper.toCohortLoanDetailEntity(cohortLoanDetail);
//        log.info("-------> Cohort Loan detail entity: {}", cohortLoanDetailEntity);
        cohortLoanDetailEntity = cohortLoanDetailRepository.save(cohortLoanDetailEntity);
        log.info("-------> SavedCohort Loan detail entity: {}", cohortLoanDetailEntity);
        return cohortLoanDetailMapper.toCohortLoanDetail(cohortLoanDetailEntity);
    }

    @Override
    public void delete(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,CohortMessages.COHORT_LOAN_DETAIL_ID.getMessage());

        cohortLoanDetailRepository.deleteById(id);
    }

    @Override
    public CohortLoanDetail findByCohortId(String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());

        CohortLoanDetailEntity cohortLoanDetailEntity =
                cohortLoanDetailRepository.findByCohortId(cohortId);
        log.info("Interest incurred during find cohort loan detail entity {}", cohortLoanDetailEntity.getInterestIncurred());
        CohortLoanDetail cohortLoanDetail = cohortLoanDetailMapper.toCohortLoanDetail(cohortLoanDetailEntity);
        cohortLoanDetail.setInterestIncurred(cohortLoanDetailEntity.getInterestIncurred());
        return cohortLoanDetail;
    }

    @Transactional
    @Override
    public void deleteAllCohortLoanDetailAssociateWithProgram(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,CohortMessages.INVALID_COHORT_ID.getMessage());

        cohortLoanDetailRepository.deleteAllByProgramId(id);
    }

}
