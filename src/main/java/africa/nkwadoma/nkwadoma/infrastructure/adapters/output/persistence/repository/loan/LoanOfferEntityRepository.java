package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanOfferEntitiy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanOfferEntityRepository extends JpaRepository<LoanOfferEntitiy,String> {
}
