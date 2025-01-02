package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
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
          join LoanOfferEntitiy loe on l.id = loe.loanee.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationIdentity.id = o.id
    
          where o.id = :organizationId
    """)
    Page<LoanProjection> findAllByOrganizationId(@Param("organizationId") String organizationId, Pageable pageable);
}
