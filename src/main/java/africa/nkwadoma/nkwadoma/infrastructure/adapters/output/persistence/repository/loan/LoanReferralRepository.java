package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
    @Query("select l from LoanReferralEntity l where l.loaneeEntity.loanee.id = :loaneeUserId")
    LoanReferralEntity findByLoaneeUserId(@Param("loaneeUserId") String loaneeUserId);
}
