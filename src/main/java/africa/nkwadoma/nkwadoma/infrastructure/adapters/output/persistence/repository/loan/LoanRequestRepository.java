package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface LoanRequestRepository extends JpaRepository<LoanRequestEntity, String> {

    @Query("""
          select lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, c.name as cohortName,
                 o.name as referredBy, lr.loanAmountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, p.name as programName
          from LoanRequestEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
    """)
    Page<LoanRequestProjection> findAllLoanRequests(Pageable pageable);

    @Query("""
          select
                 lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, l.userIdentity.email as email, l.userIdentity.alternateContactAddress as alternateContactAddress,
                 l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, o.name as referredBy, l.id as loaneeId,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, c.tuitionAmount as tuitionAmount, c.name as cohortName, l.userIdentity.image as loaneeImage,
                 p.name as programName, n.id as nextOfKinId, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName, n.contactAddress as nextOfKinContactAddress,
                 n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber, n.nextOfKinRelationship as nextOfKinRelationship,
                 l.loaneeLoanDetail.amountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 l.userIdentity.gender as gender, l.userIdentity.maritalStatus as maritalStatus,
                 l.userIdentity.dateOfBirth as dateOfBirth, l.userIdentity.residentialAddress as residentialAddress, l.userIdentity.nationality as nationality,
                 l.userIdentity.stateOfOrigin as stateOfOrigin, l.userIdentity.stateOfResidence as stateOfResidence, lr.status as status

          from LoanRequestEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          left join NextOfKinEntity n on l.id = n.loaneeEntity.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
          where lr.id = :id
    """)
    Optional<LoanRequestProjection> findLoanRequestById(@Param("id") String id);

    @Query("""
          select lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, c.name as cohortName,
                 o.name as referredBy, lr.loanAmountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, p.name as programName
          from LoanRequestEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
          where o.id = :organizationId
    """)
    Page<LoanRequestProjection> findAllLoanRequestsByOrganizationId(Pageable pageable, @Param("organizationId") String organizationId);
}
