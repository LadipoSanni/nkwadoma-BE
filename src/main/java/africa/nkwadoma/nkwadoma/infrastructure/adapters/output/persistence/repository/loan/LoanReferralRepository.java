package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
}
