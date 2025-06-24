package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortLoaneeRepository extends JpaRepository<CohortLoaneeEntity, String> {
    CohortLoaneeEntity findCohortLoaneeEntityByLoanee_IdAndCohort_Id(String loaneeId, String cohortId);
}
