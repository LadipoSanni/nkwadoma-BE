package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductDisbursementRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanProductDisbursementRuleRepository extends JpaRepository<LoanProductDisbursementRuleEntity,String> {
}
