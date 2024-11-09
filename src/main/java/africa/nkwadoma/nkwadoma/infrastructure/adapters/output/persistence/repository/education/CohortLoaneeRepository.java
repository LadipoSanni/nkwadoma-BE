package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CohortLoaneeRepository extends JpaRepository<CohortLoaneeEntity,String> {
    List<CohortLoanee> findAllByCohort(String id);
}
