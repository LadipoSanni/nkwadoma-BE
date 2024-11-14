package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
    Page<LoanReferralEntity> findByLoaneeEntityId(String loaneeId, Pageable pageable);
}
