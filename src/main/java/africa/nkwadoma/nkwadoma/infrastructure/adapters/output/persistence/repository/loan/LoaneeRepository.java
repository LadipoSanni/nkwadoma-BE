package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface LoaneeRepository extends JpaRepository<LoaneeEntity,String> {
    LoaneeEntity findLoaneeByUserIdentityEmail(String email);

    Optional<LoaneeEntity> findLoaneeByUserIdentityId(String userId);

    Page<LoaneeEntity> findAllByCohortId(String cohortId, Pageable pageable);

    List<LoaneeEntity> findAllLoaneesByCohortId(String id);
}
