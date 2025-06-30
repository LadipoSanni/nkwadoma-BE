package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.OrganizationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

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
                when 'LOAN_OFFER' then coalesce(lm.loanOfferCount, 0)
                when 'LOAN_REQUEST' then coalesce(lm.loanRequestCount, 0)
                when 'LOAN_DISBURSAL' then coalesce(lm.loanDisbursalCount, 0)
            else 0
        end desc,
        o.name asc
""")
    Page<OrganizationProjection> findAllWithLoanMetrics(@Param("loanType") String loanType, Pageable pageable);

    @Query(""" 
            SELECT o.id as organizationId,
                   ld.totalAmountReceived as totalAmountReceived,
                   ld.totalAmountRequested as totalAmountRequested,
                   ld.totalAmountRepaid as totalDebtRepaid,
                   ld.totalOutstandingAmount as totalCurrentDebt
                   from OrganizationEntity o
                               
                   join OrganizationLoanDetailEntity ld on ld.organization.id = o.id        
                   WHERE UPPER(o.status) = UPPER(:status) 
                        """)
    Page<OrganizationProjection> findAllByStatus(@Param("status") String status, Pageable pageable);


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
        where lower(o.name) like lower(concat('%', :name, '%'))
        and o.status = 'ACTIVE'
        order by 
            case :loanType
                when 'LOAN_OFFER' then coalesce(lm.loanOfferCount, 0)
                when 'LOAN_REQUEST' then coalesce(lm.loanRequestCount, 0)
                when 'LOAN_DISBURSAL' then coalesce(lm.loanDisbursalCount, 0)
                else 0
            end desc,
            o.name asc
    """)
    Page<OrganizationProjection> searchOrganizationSortingWithLoanType(
            @Param("name") String name,
            @Param("loanType") String loanType,
            Pageable pageRequest
    );
    @Query("""
    SELECT o FROM OrganizationEntity o
    JOIN ProgramEntity p ON p.organizationIdentity.id = o.id
    JOIN CohortEntity c ON c.programId = p.id
    WHERE c.id = :cohortId
""")
    Optional<OrganizationEntity> findByCohortId(String cohortId);


    @Query("""
    Select o.id as organizationId,
           ld.totalAmountReceived as totalAmountReceived,
           ld.totalAmountRequested as totalAmountRequested,
           ld.totalAmountRepaid as totalDebtRepaid,
           ld.totalOutstandingAmount as totalCurrentDebt
                  
          from OrganizationEntity o
          join OrganizationLoanDetailEntity ld on ld.organization.id = o.id    
           
    """)
    Page<OrganizationProjection> findAllOrganization(Pageable pageRequest);
}
