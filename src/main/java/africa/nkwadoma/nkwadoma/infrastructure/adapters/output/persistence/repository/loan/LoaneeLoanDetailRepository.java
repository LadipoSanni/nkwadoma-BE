package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeLoanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaneeLoanDetailRepository extends JpaRepository<LoaneeLoanDetailEntity, String> {
}
