package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    @Query("""
          select
          le.id as id,
          le.startDate as loanStartDate,
          l.userIdentity.firstName as firstName,
          l.userIdentity.lastName as lastName,
          l.loaneeLoanDetail.initialDeposit as initialDeposit,
          lr.loanAmountRequested as loanAmountRequested,
          lr.createdDate as createdDate,
          c.startDate as cohortStartDate,
          p.name as programName,
          o.name as referredBy
    
          from LoanEntity le
          join LoaneeEntity l on le.loaneeEntity.id = l.id
          join LoanRequestEntity lr on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
    
          where o.id = :organizationId
    """)
    Page<LoanProjection> findAllByOrganizationId(@Param("organizationId") String organizationId, Pageable pageable);
}
