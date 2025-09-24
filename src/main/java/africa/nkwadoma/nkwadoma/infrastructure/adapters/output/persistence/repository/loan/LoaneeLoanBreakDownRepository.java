package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanBreakdownEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoaneeLoanBreakDownRepository extends JpaRepository<LoaneeLoanBreakdownEntity,String> {
    List<LoaneeLoanBreakdownEntity> findAllByCohortLoaneeId(String cohortLoaneeId);

    void deleteAllByCohortLoaneeId(String id);
}
