package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface LoanRequestRepository extends JpaRepository<LoanRequestEntity, String> {

    @Query("""
          select lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, c.name as cohortName,
                 o.name as referredBy, lr.loanAmountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 cle.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, p.name as programName
          from LoanRequestEntity lr
          join LoanReferralEntity  lre on lr.id = lr.id
          join CohortLoaneeEntity cle on cle.id = lre.id
          join LoaneeEntity l on l.id = cle.loanee.id
          join CohortEntity c on cle.cohort.id = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
          where lr.status = 'NEW' and l.userIdentity.isIdentityVerified = true
    """)
    Page<LoanRequestProjection> findAllLoanRequests(Pageable pageable);

    @Query("""
    SELECT 
        lr.id as id, l.userIdentity.firstName as firstName,  l.userIdentity.lastName as lastName,  l.userIdentity.email as email,
        l.userIdentity.phoneNumber as phoneNumber,l.userIdentity.alternateContactAddress as alternateContactAddress,
         l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, 
         o.name as referredBy,  l.id as loaneeId, cle.loaneeLoanDetail.initialDeposit as initialDeposit , c.startDate as cohortStartDate,
         c.tuitionAmount as tuitionAmount, c.name as cohortName, l.userIdentity.image , p.name as programName, n.id as nextOfKinId, 
        n.firstName as nextOfKinFirstName, 
        n.lastName as nextOfKinLastName, 
        n.contactAddress as nextOfKinContactAddress,
        n.email as nextOfKinEmail , 
        n.phoneNumber as nextOfKinPhoneNumber, 
        n.nextOfKinRelationship as nextOfKinRelationship,
        cle.loaneeLoanDetail.amountRequested as loanAmountRequested, 
        lr.createdDate as createdDate,
        l.userIdentity.gender as gender, 
        l.userIdentity.maritalStatus as maritalStatus,
        l.userIdentity.dateOfBirth as dateOfBirth, 
        l.userIdentity.residentialAddress as residentialAddress, 
        l.userIdentity.nationality as nationality,
        l.userIdentity.stateOfOrigin as stateOfOrigin, 
        l.userIdentity.stateOfResidence as stateOfResidence, 
        lr.status as status, cle.id as cohortLoaneeId,
        u.isIdentityVerified as isVerified, cle.onboardingMode as onboardingMode
    
    FROM LoanRequestEntity lr
    JOIN LoanReferralEntity lre ON lr.id = lre.id
    JOIN CohortLoaneeEntity cle ON lre.cohortLoanee.id = cle.id
    JOIN LoaneeEntity l ON cle.loanee.id = l.id
    JOIN UserEntity u ON l.userIdentity.id = u.id
    JOIN CohortEntity c ON cle.cohort.id = c.id
    LEFT JOIN NextOfKinEntity n ON u.nextOfKinEntity.id = n.id
    JOIN ProgramEntity p ON c.programId = p.id
    JOIN OrganizationEntity o ON p.organizationIdentity.id = o.id
    WHERE lr.id = :id
""")
    Optional<LoanRequestProjection> findLoanRequestById(@Param("id") String id);

    @Query("""
          select lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, c.name as cohortName,
                 o.name as referredBy, lr.loanAmountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 cle.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, p.name as programName
          from LoanRequestEntity lr
          join LoanReferralEntity  lre on lr.id = lr.id
          join CohortLoaneeEntity cle on cle.id = lre.id
          join LoaneeEntity l on l.id = cle.loanee.id  
          join CohortEntity c on cle.cohort.id = c.id          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
          where lr.status = 'NEW' AND o.id = :organizationId AND l.userIdentity.isIdentityVerified = true
    """)
    Page<LoanRequestProjection> findAllLoanRequestsByOrganizationId(Pageable pageable, @Param("organizationId") String organizationId);


    @Query("""
    SELECT lr.id AS id,
           lr.createdDate AS createdDate,
           u.firstName AS firstName,
           u.lastName AS lastName,
           cle.loaneeLoanDetail.amountRequested AS loanAmountRequested,
           cle.loaneeLoanDetail.initialDeposit AS initialDeposit,
           c.name AS cohortName,
           c.startDate AS cohortStartDate,
           p.name AS programName
               
    FROM LoanRequestEntity lr 
    join LoanReferralEntity  lre on lr.id  = lr.id
          join CohortLoaneeEntity cle on cle.id = lre.id
          join LoaneeEntity l on l.id = cle.loanee.id  
    JOIN l.userIdentity u
    join CohortEntity c on cle.cohort.id = c.id
    JOIN ProgramEntity p ON p.id = c.programId
    WHERE 
        (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
         OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
        AND c.programId = :programId
        AND p.organizationIdentity.id = :organizationId
        AND lr.status <> 'APPROVED'
    """)
    Page<LoanRequestProjection> findAllLoanRequestByLoaneeNameInOrganizationAndProgram(@Param("programId") String programId,
                                                                                       @Param("organizationId") String organizationId,
                                                                                       @Param("name") String name,
                                                                                       Pageable pageRequest);

    @Query("""
    SELECT lr.id AS id,
           lr.createdDate AS createdDate,
           u.firstName AS firstName,
           u.lastName AS lastName,
           cle.loaneeLoanDetail.amountRequested AS loanAmountRequested,
           cle.loaneeLoanDetail.initialDeposit AS initialDeposit,
           c.name AS cohortName,
           c.startDate AS cohortStartDate,
           p.name AS programName
    FROM LoanRequestEntity lr
    join LoanReferralEntity  lre on lr.id= lr.id
          join CohortLoaneeEntity cle on cle.id = lre.id
          join LoaneeEntity l on l.id = cle.loanee.id
    JOIN l.userIdentity u
          join CohortEntity c on cle.cohort.id = c.id    JOIN ProgramEntity p ON p.id = c.programId
    WHERE
        c.programId = :programId
        AND p.organizationIdentity.id = :organizationId
        AND cle.loaneeStatus <> 'APPROVED'
    """)
    Page<LoanRequestProjection> filterLoanRequestByProgramIdAndOrganization(@Param("programId") String programId,
                                                                            @Param("organizationId") String organizationId,
                                                                            Pageable pageRequest);

    @Query("""
          select
                 lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, l.userIdentity.email as email, l.userIdentity.phoneNumber as phoneNumber, l.userIdentity.alternateContactAddress as alternateContactAddress,
                 l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, o.name as referredBy, l.id as loaneeId,
                 cle.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, c.tuitionAmount as tuitionAmount, c.name as cohortName, l.userIdentity.image as loaneeImage,
                 p.name as programName, n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName, n.contactAddress as nextOfKinContactAddress,
                 n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber, n.nextOfKinRelationship as nextOfKinRelationship,
                 cle.loaneeLoanDetail.amountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 l.userIdentity.gender as gender, l.userIdentity.maritalStatus as maritalStatus,
                 l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
                 l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence, lr.status as status

          from LoanRequestEntity lr
          
          join LoanReferralEntity  lre on lr.id   = lr.id 
          join CohortLoaneeEntity cle on cle.id = lre.id
          join LoaneeEntity l on l.id = cle.loanee.id      
          join CohortEntity c on cle.cohort.id = c.id          left join NextOfKinEntity n
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
          where l.id = :loaneeId
    """)
    Optional<LoanRequestProjection> findLoanRequestByLoaneeEntityId(@Param("loaneeId") String loaneeId);

    @Query("""
    SELECT COUNT(lr.id)
    FROM LoanRequestEntity lr
    join LoanReferralEntity  lre on lr.id   = lr.id
    join CohortLoaneeEntity cle on cle.id = lre.id
    join LoaneeEntity l on l.id = cle.loanee.id  
    join CohortEntity c on cle.cohort.id = c.id    JOIN ProgramEntity p ON c.programId = p.id
    JOIN OrganizationEntity o ON p.organizationIdentity.id = o.id
    WHERE lr.status = 'NEW' AND o.id = :organizationId AND l.userIdentity.isIdentityVerified = true
""")
    int getCountOfVerifiedLoanRequestInOrganization(@Param("organizationId") String organizationId);
}
