package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CohortLoanDetailsRepository extends JpaRepository<CohortLoanDetailEntity,String> {
    CohortLoanDetailEntity findByCohort(String cohortEntity);
}
