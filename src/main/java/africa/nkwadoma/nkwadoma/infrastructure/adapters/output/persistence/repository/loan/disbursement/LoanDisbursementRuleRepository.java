package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.LoanDisbursementRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanDisbursementRuleRepository extends JpaRepository<LoanDisbursementRuleEntity,String> {
    List<LoanDisbursementRuleEntity> findAllByLoanEntity_IdAndDisbursementRuleEntity_Id(String loanEntityId, String disbursementRuleEntityId);

    List<LoanDisbursementRuleEntity> findByLoanEntity_IdAndDisbursementRuleStatus(String loanId, DisbursementRuleStatus disbursementRuleStatus);

    void deleteAllByLoanEntity_Id(String loanId);
}
