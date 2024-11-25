package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

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
          select lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName,
                 o.name as referredBy, lr.loanAmountRequested as loanAmountRequested,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, p.name as programName
          from LoanRequestEntity lr 
          join LoaneeEntity l on lr.loaneeEntity.id = l.id 
          join CohortEntity c on l.cohortId = c.id 
          join ProgramEntity p on c.programId = p.id 
          join OrganizationEntity o on p.organizationEntity.id = o.id
    """)
    LoanRequestProjection findLoanRequestById(String id);
}
