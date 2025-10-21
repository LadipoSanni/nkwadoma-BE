package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.LoanDisbursementRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoanDisbursementRuleRepository extends JpaRepository<LoanDisbursementRuleEntity,String> {
    List<LoanDisbursementRuleEntity> findAllByLoanEntity_IdAndDisbursementRuleEntity_Id(String loanEntityId, String disbursementRuleEntityId);

    List<LoanDisbursementRuleEntity> findByLoanEntity_IdAndDisbursementRuleStatus(String loanId, DisbursementRuleStatus disbursementRuleStatus);

    void deleteAllByLoanEntity_Id(String loanId);

    @Query(
            value = """
        SELECT CASE 
            WHEN COUNT(*) = 0 THEN TRUE 
            ELSE FALSE 
        END
        FROM loan_disbursement_rule_entity l
        WHERE l.loan_entity_id = :loanId
          AND l.disbursement_rule_status = 'EXECUTED'
        """,
            nativeQuery = true
    )
    boolean isDisbursementRuleRemovable(String loanId);
}
