package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CohortLoaneePersistenceAdapter implements CohortLoaneeOutputPort {

    private final CohortLoaneeRepository cohortLoaneeRepository;
    private final CohortLoaneeMapper cohortLoaneeMapper;


    @Override
    public CohortLoanee save(Loanee loanee) throws MeedlException {
        CohortLoanee cohortLoanee = new CohortLoanee();
        cohortLoanee.setCohort(loanee.getCohortId());
        cohortLoanee.setLoanee(loanee);
        MeedlValidator.validateObjectInstance(cohortLoanee);
        cohortLoanee.validate();
        CohortLoaneeEntity cohortLoaneeEntity = cohortLoaneeMapper.toCohortLoaneeEntity(cohortLoanee);
        cohortLoaneeEntity = cohortLoaneeRepository.save(cohortLoaneeEntity);
        cohortLoanee = cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
        return cohortLoanee;
    }

    @Override
    public List<CohortLoanee> findAllLoaneesByCohortId(Cohort foundCohort) {
        return cohortLoaneeRepository.findAllByCohort(foundCohort.getId());
    }
}
