package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoanDetailsMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoanDetailsRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CohortLoanDetailsPersistenceAdapter implements CohortLoanDetailsOutputPort {

    private final CohortLoanDetailsRepository cohortLoanDetailsRepository;
    private final CohortLoanDetailsMapper cohortLoanDetailsMapper;
    private final LoanDetailsOutputPort loanDetailsOutputPort;

    @Override
    public CohortLoanDetail findByCohort(String id) {
        CohortLoanDetailEntity cohortLoanDetailEntity =
                cohortLoanDetailsRepository.findByCohort(id);
        return cohortLoanDetailsMapper.toCohortLoanDetails(cohortLoanDetailEntity);
    }

    @Override
    public CohortLoanDetail saveCohortLoanDetails(Cohort cohort, String id) throws MeedlException {
        LoanDetail loanDetail = loanDetailsOutputPort.saveLoanDetails(cohort.getCohortLoanDetail().getLoanDetail());
        CohortLoanDetail cohortLoanDetail = new CohortLoanDetail();
        cohortLoanDetail.setLoanDetail(loanDetail);
        cohortLoanDetail.setCohort(id);
        CohortLoanDetailEntity cohortLoanDetailEntity =
                cohortLoanDetailsMapper.toCohortLoanDetailsEntity(cohortLoanDetail);
        cohortLoanDetailsRepository.save(cohortLoanDetailEntity);
        return cohortLoanDetailsMapper.toCohortLoanDetails(cohortLoanDetailEntity);
    }
}
