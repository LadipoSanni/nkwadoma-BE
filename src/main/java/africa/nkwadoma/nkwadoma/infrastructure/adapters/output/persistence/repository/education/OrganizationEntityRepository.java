package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
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
            "AND (:status IS NULL OR o.activationStatus = :status)")
    Page<OrganizationEntity> findByNameContainingIgnoreCaseAndStatus(@Param("name") String name,
                                                                     @Param("status") ActivationStatus status,
                                                                     Pageable pageable);

    Optional<OrganizationEntity> findByTaxIdentity(String tin);

    Optional<OrganizationEntity> findByName(String name);

    @Query("""
    select o.id as organizationId,
           o.name as name,
           o.logoImage as logoImage,
           institute.numberOfLoanees as numberOfLoanees,
           institute.numberOfCohort as numberOfCohort,
           institute.numberOfPrograms as numberOfPrograms,
           institute.stillInTraining as stillInTraining,o.phoneNumber as phoneNumber,
           o.taxIdentity as taxIdentity , o.bannerImage as bannerImage, o.address as address,
           lm.loanRequestCount as loanRequestCount,
           lm.loanDisbursalCount as loanDisbursalCount,
           lm.loanOfferCount as loanOfferCount,
           lm.loanReferralCount as loanReferralCount
    from OrganizationEntity o
    left join InstituteMetricsEntity  institute on institute.organization.id = o.id
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
                   o.name as name,
                   ld.amountReceived as totalAmountReceived,
                   ld.amountRequested as totalAmountRequested,
                   ld.amountRepaid as totalDebtRepaid,
                   ld.outstandingAmount as totalCurrentDebt,
                    o.activationStatus as activationStatus,o.email as email,
                    institute.numberOfLoanees as numberOfLoanees,
                    o.invitedDate as invitedDate,
                    o.rcNumber as rcNumber,o.phoneNumber as phoneNumber,
                    o.taxIdentity as taxIdentity , o.bannerImage as bannerImage, o.address as address,
                    institute.numberOfCohort as numberOfCohort,o.websiteAddress as websiteAddress,
                    institute.numberOfPrograms as numberOfPrograms,
                    institute.stillInTraining as stillInTraining,
        
                      CASE
            WHEN COALESCE(ld.amountReceived, 0) = 0 THEN 0.0 
            ELSE (ld.outstandingAmount / ld.amountReceived * 100.0) 
        END as debtPercentage,
        CASE 
            WHEN COALESCE(ld.amountReceived, 0) = 0 THEN 0.0 
            ELSE (ld.amountRepaid / ld.amountReceived * 100.0) 
        END as repaymentRate,
             
                   CONCAT(u.firstName, ' ', u.lastName) as inviterFullName
                   from OrganizationEntity o
                  left join InstituteMetricsEntity  institute on institute.organization.id = o.id                            
                   join OrganizationLoanDetailEntity ld on ld.organization.id = o.id  
                   JOIN UserEntity u ON u.id = o.createdBy      
                   WHERE UPPER(o.activationStatus) IN :activationStatuses
                        """)
    Page<OrganizationProjection> findAllByStatus(@Param("activationStatuses") List<String> activationStatuses, Pageable pageable);


    @Query("""
        select o.id as organizationId,
               o.name as name,
               o.logoImage as logoImage,
               institute.numberOfLoanees as numberOfLoanees,
               institute.numberOfCohort as numberOfCohort,
               institute.stillInTraining as stillInTraining,
               o.rcNumber as rcNumber,o.phoneNumber as phoneNumber,
               o.taxIdentity as taxIdentity , o.bannerImage as bannerImage, o.address as address,
               institute.numberOfPrograms as numberOfPrograms,
               lm.loanRequestCount as loanRequestCount,
               lm.loanDisbursalCount as loanDisbursalCount,
               lm.loanOfferCount as loanOfferCount,
               lm.loanReferralCount as loanReferralCount
        from OrganizationEntity o
        left join InstituteMetricsEntity  institute on institute.organization.id = o.id

        join LoanMetricsEntity lm on lm.organizationId = o.id
        where lower(o.name) like lower(concat('%', :name, '%'))
        and o.activationStatus = 'ACTIVE'
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
    SELECT 
        o.id as organizationId,
        o.name as name,
        ld.amountReceived as totalAmountReceived,
        ld.amountRequested as totalAmountRequested,
        ld.amountRepaid as totalDebtRepaid,
        ld.outstandingAmount as totalCurrentDebt,
        o.activationStatus as status,
        o.email as email,o.phoneNumber as phoneNumber,
        o.taxIdentity as taxIdentity , o.bannerImage as bannerImage, o.address as address,
        institute.numberOfLoanees as numberOfLoanees,
        o.websiteAddress as websiteAddress,
        institute.numberOfCohort as numberOfCohort,
        institute.numberOfPrograms as numberOfPrograms,
        institute.stillInTraining as stillInTraining,
        CASE 
            WHEN COALESCE(ld.amountReceived, 0) = 0 THEN 0.0 
            ELSE (ld.outstandingAmount / ld.amountReceived * 100.0) 
        END as debtPercentage,
        CASE 
            WHEN COALESCE(ld.amountReceived, 0) = 0 THEN 0.0 
            ELSE (ld.amountRepaid / ld.amountReceived * 100.0) 
        END as repaymentRate
    FROM OrganizationEntity o
    left join InstituteMetricsEntity  institute on institute.organization.id = o.id                            
    LEFT JOIN OrganizationLoanDetailEntity ld ON ld.organization.id = o.id
     WHERE NOT EXISTS (
            SELECT f
            FROM FinancierEntity f
            WHERE f.identity = o.id
              AND f.financierType = 'COOPERATE'
        )
""")
    Page<OrganizationProjection> findAllOrganization(Pageable pageRequest);

    @Query("""
    SELECT org
    FROM OrganizationEntity org
    JOIN OrganizationEmployeeEntity emp ON emp.organization = org.id
    WHERE emp.meedlUser.id = :userId
""")
    Optional<OrganizationEntity> findByUserId(String userId);


    @Query("""
        select o.id as organizationId,
               o.name as name,
               o.logoImage as logoImage,
               institute.numberOfLoanees as numberOfLoanees,
               institute.numberOfCohort as numberOfCohort,
               institute.stillInTraining as stillInTraining,
               o.rcNumber as rcNumber,o.phoneNumber as phoneNumber,
               o.taxIdentity as taxIdentity , o.bannerImage as bannerImage, o.address as address,
               institute.numberOfPrograms as numberOfPrograms,
               lm.loanRequestCount as loanRequestCount,
               lm.loanDisbursalCount as loanDisbursalCount,
               lm.loanOfferCount as loanOfferCount,
               lm.loanReferralCount as loanReferralCount,
               o.activationStatus as activationStatus , o.email as email, o.websiteAddress as websiteAddress,
               o.invitedDate as invitedDate, loanDetail.amountReceived as totalAmountReceived,
               loanDetail.amountRequested as totalAmountRequested, loanDetail.amountRepaid as totalDebtRepaid,
               loanDetail.outstandingAmount as totalCurrentDebt, loanDetail.amountReceived as totalHistoricalDebt         
               
                               
        from  OrganizationEntity  o
                left join InstituteMetricsEntity institute on institute.organization.id = o.id
                join LoanMetricsEntity lm on lm.organizationId = o.id  
                join OrganizationLoanDetailEntity loanDetail on loanDetail.organization.id = o.id               
                where o.id = :organizationId
        """)
    OrganizationProjection findByIdProjection(@Param("organizationId") String organizationId);


    @Query("""
    SELECT COUNT(o) > 0
    FROM OrganizationEntity o
    WHERE lower(o.email) = lower(:email)
""")
    boolean existsByEmail(@Param("email") String email);

}
