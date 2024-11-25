package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import javax.swing.text.html.*;
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
                 lr.id, l.userIdentity.firstName, l.userIdentity.lastName, l.userIdentity.alternateContactAddress,
                 l.userIdentity.alternateEmail, l.userIdentity.alternatePhoneNumber, o.name, l.id as loaneeId,
                 lr.loanAmountRequested, l.loaneeLoanDetail.initialDeposit, c.startDate, c.name, p.name
    
          from LoanRequestEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationEntity.id = o.id
          where lr.id = :id
    """)
    Optional<LoanRequestProjection> findLoanRequestById(@Param("id") String id);
}
