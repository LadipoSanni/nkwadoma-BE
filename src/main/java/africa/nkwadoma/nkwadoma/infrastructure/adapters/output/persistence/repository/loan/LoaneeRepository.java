package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface LoaneeRepository extends JpaRepository<LoaneeEntity,String> {
    LoaneeEntity findLoaneeByUserIdentityEmail(String email);
    List<LoaneeEntity> findAllByCohortId(String id);
    Optional<LoaneeEntity> findLoaneeByUserIdentityId(String userId);
}
