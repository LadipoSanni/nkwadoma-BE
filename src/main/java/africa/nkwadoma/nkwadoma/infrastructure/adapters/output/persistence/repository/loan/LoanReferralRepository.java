package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.jpa.repository.*;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
    LoanReferralEntity findByLoaneeEntityId(String loanReferralId);
}
