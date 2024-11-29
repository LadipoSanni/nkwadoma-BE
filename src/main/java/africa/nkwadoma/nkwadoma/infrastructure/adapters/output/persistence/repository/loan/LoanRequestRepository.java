package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface LoanRequestRepository extends JpaRepository<LoanRequestEntity, String> {

    @Query("""
          select lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName,
                 o.name as referredBy, lr.loanAmountRequested as loanAmountRequested, lr.createdDate as createdDate,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, p.name as programName
          from LoanRequestEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationEntity.id = o.id
    """)
    Page<LoanRequestProjection> findAllLoanRequests(Pageable pageable);

    @Query("""
          select
                 lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, l.userIdentity.alternateContactAddress as alternateContactAddress,
                 l.userIdentity.alternateEmail as alternateEmail, l.userIdentity.alternatePhoneNumber as alternatePhoneNumber, o.name as referredBy, l.id as loaneeId,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, c.name as cohortName,
                 p.name as programName, n.firstName as nextOfKinFirstName, n.lastName as nextOfKinLastName, n.contactAddress as nextOfKinContactAddress,
                 n.email as nextOfKinEmail, n.phoneNumber as nextOfKinPhoneNumber, n.nextOfKinRelationship as nextOfKinRelationship,
                 l.loaneeLoanDetail.amountRequested as loanAmountRequested, l.loaneeLoanDetail.tuitionAmount as tuitionAmount, lr.createdDate as createdDate

          from LoanRequestEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join NextOfKinEntity n on l.id = n.loaneeEntity.id
          join LoanBreakdownEntity lb on c.id = lb.cohort.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationEntity.id = o.id
          where lr.id = :id
    """)
    Optional<LoanRequestProjection> findLoanRequestById(@Param("id") String id);
}
