package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
    LoanReferralEntity findByLoaneeEntityId(String loanReferralId);

    @Query("""
          select
                 lr.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName, o.name as referredBy,
                 l.loaneeLoanDetail.initialDeposit as initialDeposit, c.startDate as cohortStartDate, c.tuitionAmount as tuitionAmount,
                 c.name as cohortName, l.userIdentity.image as loaneeImage,
                 p.name as programName, l.loaneeLoanDetail.amountRequested as loanAmountRequested

          from LoanReferralEntity lr
          join LoaneeEntity l on lr.loaneeEntity.id = l.id
          join CohortEntity c on l.cohortId = c.id
          join ProgramEntity p on c.programId = p.id
          join OrganizationEntity o on p.organizationEntity.id = o.id
          where lr.id = :id
    """)
    Optional<LoanReferralProjection> findLoanReferralById(@Param("id") String id);
}
