package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.LoanDisbursementRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanDisbursementRuleRepository extends JpaRepository<LoanDisbursementRuleEntity,String> {
    List<LoanDisbursementRuleEntity> findAllByLoanEntity_IdAndDisbursementRuleEntity_Id(String loanEntityId, String disbursementRuleEntityId);
}
