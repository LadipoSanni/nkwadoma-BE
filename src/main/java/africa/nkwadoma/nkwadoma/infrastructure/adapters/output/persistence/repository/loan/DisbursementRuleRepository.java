package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DisbursementRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisbursementRuleRepository extends JpaRepository<DisbursementRuleEntity,String> {
}
