package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanType;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationEntityRepository extends JpaRepository<OrganizationEntity,String> {
    Optional<OrganizationEntity> findByEmail(String email);

    Optional<OrganizationEntity> findByRcNumber(String rcNumber);

    @Query("SELECT o FROM OrganizationEntity o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND (:status IS NULL OR o.status = :status)")
    Page<OrganizationEntity> findByNameContainingIgnoreCaseAndStatus(@Param("name") String name,
                                                                     @Param("status") ActivationStatus status,
                                                                     Pageable pageable);

    Optional<OrganizationEntity> findByTaxIdentity(String tin);

    Optional<OrganizationEntity> findByName(String name);

    @Query("""
    select o.id as organizationId,
           o.name as name,
           o.logoImage as logoImage,
           o.numberOfLoanees as numberOfLoanees,
           o.numberOfCohort as numberOfCohort,
           o.numberOfPrograms as numberOfPrograms,
           lm.loanRequestCount as loanRequestCount,
           lm.loanDisbursalCount as loanDisbursalCount,
           lm.loanOfferCount as loanOfferCount,
           lm.loanReferralCount as loanReferralCount
    from OrganizationEntity o
    join LoanMetricsEntity lm on lm.organizationId = o.id
    order by 
        case :loanType
            when 'LOAN_OFFER' then lm.loanOfferCount
            when 'LOAN_REQUEST' then lm.loanRequestCount
            when 'LOAN_DISBURSAL' then lm.loanDisbursalCount
            else 0
        end desc,
        o.name asc
""")
    Page<OrganizationProjection> findAllWithLoanMetrics(@Param("loanType") String loanType, Pageable pageable);

    @Query("SELECT o FROM OrganizationEntity o WHERE UPPER(o.status) = UPPER(:status) ")
    Page<OrganizationEntity> findAllByStatus(@Param("status") String status, Pageable pageable);


}
