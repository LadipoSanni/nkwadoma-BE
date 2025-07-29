package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.math.BigDecimal;
import java.util.*;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    @Query("""
          select
                 le.id as id, l.id as loaneeId, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, l.userIdentity.email as email,
                 l.userIdentity.phoneNumber as phoneNumber, l.userIdentity.alternateContactAddress as alternateContactAddress,
                 l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, o.name as referredBy,
                 cle.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, c.tuitionAmount as tuitionAmount, c.name as cohortName, l.userIdentity.image as loaneeImage,
                 p.name as programName, n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName, n.contactAddress as nextOfKinContactAddress,
                 n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber, n.nextOfKinRelationship as nextOfKinRelationship,
                 cle.loaneeLoanDetail.amountRequested as loanAmountRequested, lr.createdDate as createdDate, lr.status as status,
                 l.userIdentity.gender as gender, l.userIdentity.maritalStatus as maritalStatus,
                 l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
                 l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence,
                 cle.id as cohortLoaneeId,loe.amountApproved as loanAmountApproved,lld.amountOutstanding as amountOutstanding,
                 lld.amountRepaid as amountRepaid ,lp.interestRate as interestRate, lld.interestIncurred as interestIncurred

          from LoanEntity le
          join LoanOfferEntity lo on lo.id = le.loanOfferId
          join LoanReferralEntity lfe on lfe.id = lo.id
          join CohortLoaneeEntity cle on cle.id = lfe.cohortLoanee.id
          join LoaneeLoanDetailEntity lld on lld.id = cle.loaneeLoanDetail.id
          join LoaneeEntity l on l.id = cle.loanee.id
          join UserEntity  u on u.id = l.userIdentity.id
          join LoanRequestEntity lr on lr.id = lfe.id
          join LoanOfferEntity loe on loe.id = lr.id
          join LoanProductEntity lp on lp.id = loe.loanProduct.id
          join CohortEntity c on c.id = cle.cohort.id
          join ProgramEntity p on p.id = c.programId
          left join NextOfKinEntity n on n.id = u.nextOfKinEntity.id
          join OrganizationEntity o on o.id = p.organizationIdentity.id
          where le.id = :id
    """)
    Optional<LoanProjection> findLoanById(@Param("id") String id);

    @Query("""
         select
             le.id as id,
             le.startDate as startDate,
             l.userIdentity.firstName as firstName,
             l.userIdentity.lastName as lastName,
             cle.loaneeLoanDetail.initialDeposit as initialDeposit,
             lr.createdDate as createdDate,
             lr.loanAmountRequested as loanAmountRequested,
             c.name as cohortName,
             c.startDate as cohortStartDate,
             loe.dateTimeOffered as offerDate,
             p.name as programName
         from LoanEntity le
         join LoanOfferEntity lo on lo.id = le.id
         join LoanReferralEntity lfe on lfe.id = lo.id
         join CohortLoaneeEntity cle on cle.id = lfe.cohortLoanee.id
         join LoaneeEntity l on l.id = cle.loanee.id
         join CohortEntity c on cle.cohort.id = c.id
         join LoanRequestEntity lr on lr.id = lfe.id
         join LoanOfferEntity loe on l.id = lr.id
         join ProgramEntity p on c.programId = p.id
         join OrganizationEntity o on p.organizationIdentity.id = o.id
         where o.id = :organizationId
    """)
    Page<LoanProjection> findAllByOrganizationId(@Param("organizationId") String organizationId, Pageable pageable);



    @Query("""
    SELECT le.id AS id,
           le.startDate as startDate,
           u.firstName AS firstName,
           u.lastName AS lastName,
           lof.dateTimeOffered AS offerDate,
           cle.loaneeLoanDetail.amountRequested AS loanAmountRequested,
           cle.loaneeLoanDetail.initialDeposit AS initialDeposit,
           cle.cohort.name AS cohortName,
           p.name AS programName
    
    FROM LoanEntity le
    join LoanOfferEntity lo on lo.id = le.id
    join LoanReferralEntity lfe on lfe.id = lo.id
    join CohortLoaneeEntity cle on cle.id = lfe.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    JOIN l.userIdentity u
    JOIN ProgramEntity p ON p.id = cle.cohort.programId
    JOIN LoanOfferEntity lof ON lof.id = le.loanOfferId
    WHERE 
        (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
         OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
        AND cle.cohort.programId = :programId
        AND p.organizationIdentity.id = :organizationId
    """)
    Page<LoanProjection> findAllLoanOfferByLoaneeNameInOrganizationAndProgram( @Param("programId") String programId,
                                                                               @Param("organizationId") String organizationId,
                                                                               @Param("name") String name, Pageable pageRequest);

    @Query("""
          select
          le.id as id,
          le.startDate as startDate,
          u.firstName as firstName,
          u.lastName as lastName,
          cle.loaneeLoanDetail.initialDeposit as initialDeposit,
          lr.createdDate as createdDate,
          lr.loanAmountRequested as loanAmountRequested,
          c.name as cohortName, c.startDate as cohortStartDate, loe.dateTimeOffered as offerDate,
          p.name as programName,lr.loanAmountApproved as loanAmountApproved,c.tuitionAmount as tuitionAmount

          from LoanEntity le
          join LoanOfferEntity loe on loe.id = le.loanOfferId
          join LoanRequestEntity lr on lr.id = loe.id
          join LoanReferralEntity lfe on lfe.id = lr.id
          join CohortLoaneeEntity cle on cle.id = lfe.cohortLoanee.id
          join LoaneeEntity l on l.id = cle.loanee.id
          join CohortEntity c on c.id = cle.cohort.id
          join ProgramEntity p on p.id = c.programId
          join UserEntity u on u.id = l.userIdentity.id
          join OrganizationEntity o on o.id = p.organizationIdentity.id
          order by le.startDate desc
    """)
    Page<LoanProjection> findAllLoan(Pageable pageRequest);

    @Query("""
          select
          le.id as id,
          le.startDate as startDate,
          l.userIdentity.firstName as firstName,
          l.userIdentity.lastName as lastName,
          cle.loaneeLoanDetail.initialDeposit as initialDeposit,
          lr.createdDate as createdDate, lr.loanAmountRequested as loanAmountRequested,
          c.name as cohortName, c.startDate as cohortStartDate, loe.dateTimeOffered as offerDate,
          p.name as programName,
          lr.loanAmountApproved as loanAmountApproved,
          c.tuitionAmount as tuitionAmount,
          o.name as referredBy
          from LoanEntity le
          join LoanOfferEntity loe on loe.id = le.loanOfferId
          join LoanRequestEntity lr on lr.id = loe.id
          join LoanReferralEntity lfe on lfe.id = lr.id
          join CohortLoaneeEntity cle on cle.id = lfe.cohortLoanee.id
          join LoaneeEntity l on l.id = cle.loanee.id
          join CohortEntity c on c.id = cle.cohort.id
          join ProgramEntity p on p.id = c.programId
          join OrganizationEntity o on o.id = p.organizationIdentity.id
          where o.id = :organizationId
          order by le.startDate desc
    """)
    Page<LoanProjection> findAllLoanInOrganization(@Param("organizationId") String organizationId, Pageable pageRequest);

    @Query("""
    SELECT lo.id AS id,
           lo.startDate as startDate,
           l.userIdentity.firstName AS firstName,
           l.userIdentity.lastName AS lastName,
           lof.dateTimeOffered AS offerDate,
           cle.loaneeLoanDetail.amountRequested AS loanAmountRequested,
           cle.loaneeLoanDetail.initialDeposit AS initialDeposit,
           c.name AS cohortName,
           p.name AS programName
    
    FROM LoanEntity lo
    join LoanOfferEntity loe on loe.id = lo.loanOfferId
    join LoanRequestEntity lr on lr.id = loe.id
    join LoanReferralEntity lfe on lfe.id = lr.id
    join CohortLoaneeEntity cle on cle.loanee.id = lfe.cohortLoanee.id
    join LoaneeEntity l on l.id = cle.loanee.id
    join CohortEntity c on cle.cohort.id = c.id
    JOIN ProgramEntity p ON p.id = c.programId
        JOIN LoanOfferEntity lof ON lo.id = l.id
    WHERE
        c.programId = :programId
        AND p.organizationIdentity.id = :organizationId
    """)
    Page<LoanProjection> filterLoanByProgramIdAndOrganization(@Param("programId") String programId,
                                                              @Param("organizationId") String organizationId, Pageable pageRequest);

    Optional<LoanEntity> findByLoanOfferId(String loanOfferId);


    @Query("""
    SELECT lo.id AS id,
           cle.referredBy AS referredBy
    FROM LoanEntity lo
    JOIN LoanOfferEntity loo ON loo.id = lo.loanOfferId
    JOIN LoanRequestEntity lr ON lr.id = loo.id
    JOIN LoanReferralEntity lfe ON lfe.id = lr.id
    JOIN CohortLoaneeEntity cle ON cle.id = lfe.cohortLoanee.id
    WHERE lo.id = :loanId
    """)
    LoanProjection findLoanReferralByLoanId(@Param("loanId") String loanId);


    @Query("""
          select
          le.id as id,
          le.startDate as startDate,
          l.userIdentity.firstName as firstName,
          l.userIdentity.lastName as lastName,
          cle.loaneeLoanDetail.initialDeposit as initialDeposit,
          lr.createdDate as createdDate, lr.loanAmountRequested as loanAmountRequested,
          c.name as cohortName, c.startDate as cohortStartDate, loe.dateTimeOffered as offerDate,
          p.name as programName,
          lr.loanAmountApproved as loanAmountApproved,
          c.tuitionAmount as tuitionAmount,
          o.name as referredBy,
          lld.amountOutstanding as amountOutstanding,
          lld.amountRepaid as amountRepaid,
              lp.interestRate as interestRate
          
              
            from LoanEntity le
          join LoanOfferEntity loe on loe.id = le.loanOfferId
           join LoanProductEntity lp on lp.id = loe.loanProduct.id
          join LoanRequestEntity lr on lr.id = loe.id
          join LoanReferralEntity lfe on lfe.id = lr.id
          join CohortLoaneeEntity cle on cle.id = lfe.cohortLoanee.id
          join LoaneeLoanDetailEntity lld on lld.id = cle.loaneeLoanDetail.id    
          join LoaneeEntity l on l.id = cle.loanee.id
          join UserEntity u on u.id = l.userIdentity.id 
          join CohortEntity c on c.id = cle.cohort.id
          join ProgramEntity p on p.id = c.programId
          join OrganizationEntity o on o.id = p.organizationIdentity.id
          where u.id = :id
          order by le.startDate desc
    """)
    Page<LoanProjection> findAllLoanDisburestToLoanee(@Param("id") String id, Pageable pageRequest);

    @Query("""
    SELECT SUM(lr.loanAmountApproved)
    FROM LoanEntity le
    JOIN LoanOfferEntity loe ON loe.id = le.loanOfferId
    JOIN LoanRequestEntity lr ON lr.id = loe.id
    JOIN LoanReferralEntity lfe ON lfe.id = lr.id
    JOIN CohortLoaneeEntity cle ON cle.id = lfe.cohortLoanee.id
    JOIN LoaneeEntity l ON l.id = cle.loanee.id
    JOIN UserEntity u ON u.id = l.userIdentity.id
    WHERE u.id = :id
""")
    BigDecimal getTotalLoanAmountApproved(@Param("id") String id);

}
