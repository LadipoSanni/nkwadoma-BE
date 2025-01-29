package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    @Query("""
          select
                 le.id as id, l.id as loaneeId, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, l.userIdentity.email as email,
                 l.userIdentity.phoneNumber as phoneNumber, l.userIdentity.alternateContactAddress as alternateContactAddress,
                 l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, o.name as referredBy,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, c.tuitionAmount as tuitionAmount, c.name as cohortName, l.userIdentity.image as loaneeImage,
                 p.name as programName, n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName, n.contactAddress as nextOfKinContactAddress,
                 n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber, n.nextOfKinRelationship as nextOfKinRelationship,
                 l.loaneeLoanDetail.amountRequested as loanAmountRequested, lr.createdDate as createdDate, lr.status as status,
                 l.userIdentity.gender as gender, l.userIdentity.maritalStatus as maritalStatus,
                 l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
                 l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence

          from LoanEntity le
          join LoaneeEntity l on le.loaneeEntity.id = l.id
          join LoanRequestEntity lr on lr.loaneeEntity.id = l.id
          join LoanOfferEntity loe on l.id = loe.loanee.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join NextOfKinEntity n on l.id = n.loaneeEntity.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
          where le.id = :id
    """)
    Optional<LoanProjection> findLoanById(@Param("id") String id);

    @Query("""
          select
          le.id as id,
          le.startDate as startDate,
          l.userIdentity.firstName as firstName,
          l.userIdentity.lastName as lastName,
          l.loaneeLoanDetail.initialDeposit as initialDeposit,
          lr.createdDate as createdDate, lr.loanAmountRequested as loanAmountRequested,
          c.name as cohortName, c.startDate as cohortStartDate, loe.dateTimeOffered as offerDate,
          p.name as programName
    
          from LoanEntity le
          join LoaneeEntity l on le.loaneeEntity.id = l.id
          join LoanRequestEntity lr on lr.loaneeEntity.id = l.id
          join LoanOfferEntity loe on l.id = loe.loanee.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
    
          where o.id = :organizationId
    """)
    Page<LoanProjection> findAllByOrganizationId(@Param("organizationId") String organizationId, Pageable pageable);



    @Query("""
    SELECT lo.id AS id,
           lo.startDate as startDate,
           u.firstName AS firstName,
           u.lastName AS lastName,
           lof.dateTimeOffered AS offerDate,
           l.loaneeLoanDetail.amountRequested AS loanAmountRequested,
           l.loaneeLoanDetail.initialDeposit AS initialDeposit,
           c.name AS cohortName,
           p.name AS programName
    
    FROM LoanEntity lo
    JOIN lo.loaneeEntity l
    JOIN l.userIdentity u
    JOIN CohortEntity c ON c.id = l.cohortId
    JOIN ProgramEntity p ON p.id = c.programId
        JOIN LoanOfferEntity lof ON lof.loanee.id = l.id
    WHERE 
        (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%'))
         OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%')))
        AND c.programId = :programId
        AND p.organizationIdentity.id = :organizationId
    """)
    Page<LoanProjection> findAllLoanOfferByLoaneeNameInOrganizationAndProgram( @Param("programId") String programId,
                                                                               @Param("organizationId") String organizationId,
                                                                               @Param("name") String name, Pageable pageRequest);
}
