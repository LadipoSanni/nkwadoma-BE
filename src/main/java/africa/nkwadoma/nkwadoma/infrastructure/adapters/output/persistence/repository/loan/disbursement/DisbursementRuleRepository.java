package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.disbursement;

import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.disbursement.DisbursementRuleStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.disbursement.DisbursementRuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface DisbursementRuleRepository extends JpaRepository<DisbursementRuleEntity,String> {
    @Query("""
    SELECT d
    FROM DisbursementRuleEntity d 
    WHERE (:#{#statuses == null || #statuses.isEmpty()} = true 
           OR d.disbursementRuleStatus IN :disbursementRuleStatus)
""")
    Page<DisbursementRuleEntity> findAllDisbursementRuleByDisbursementRuleStatuses(Set<DisbursementRuleStatus> disbursementRuleStatus, Pageable pageRequest);

    Boolean existsByNameIgnoreCase(String name);

    @Query("""
    SELECT d FROM DisbursementRuleEntity d
    WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
      AND ( :disbursementRuleStatuses IS NULL OR d.disbursementRuleStatus IN :disbursementRuleStatuses )
    """)
    Page<DisbursementRuleEntity> searchByNameAndDisbursementRuleStatuses(
            @Param("name") String name,
            @Param("disbursementRuleStatuses") Set<DisbursementRuleStatus> disbursementRuleStatus,
            Pageable pageable
    );
}
