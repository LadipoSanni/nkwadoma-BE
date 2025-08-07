package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanAggregateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoaneeLoanAggregateRepository extends JpaRepository<LoaneeLoanAggregateEntity, String> {

}
