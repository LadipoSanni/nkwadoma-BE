package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaneeLoanAccountRepository extends JpaRepository<LoaneeLoanAccountEntity,String> {
    LoaneeLoanAccountEntity findByLoaneeId(String loaneeId);
}
