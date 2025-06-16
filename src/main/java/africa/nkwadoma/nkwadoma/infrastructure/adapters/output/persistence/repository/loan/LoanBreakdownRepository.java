package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanBreakdownEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanBreakdownRepository extends JpaRepository<LoanBreakdownEntity,String> {
    void deleteAllByCohort(CohortEntity cohortEntity);

    List<LoanBreakdownEntity> findAllByCohortId(String id);

}
