package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.DisbursementRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface DisbursementRuleRepository extends JpaRepository<DisbursementRuleEntity,String> {
    @Query("""
    SELECT d
    FROM DisbursementRuleEntity d 
    WHERE (:#{#statuses == null || #statuses.isEmpty()} = true 
           OR d.activationStatus IN :activationStatuses)
""")
    Page<DisbursementRuleEntity> findAllDisbursementRuleByActivationStatuses(Set<ActivationStatus> activationStatuses, Pageable pageRequest);
}
