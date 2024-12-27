package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.jpa.repository.*;

import java.util.*;

public interface LoanMetricsRepository extends JpaRepository<LoanMetricsEntity, String> {
    Optional<LoanMetricsEntity> findDistinctTopByOrderByLoanRequestCountDesc();
}
