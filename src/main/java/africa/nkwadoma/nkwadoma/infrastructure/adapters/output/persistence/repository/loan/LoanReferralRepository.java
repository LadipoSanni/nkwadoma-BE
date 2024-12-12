package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanReferralEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

import java.util.*;

import java.util.List;

public interface LoanReferralRepository extends JpaRepository<LoanReferralEntity, String> {
    LoanReferralEntity findByLoaneeEntityId(String loanReferralId);

    @Query("""
        select lre.id as id, l.userIdentity.firstName as firstName, l.userIdentity.lastName as lastName,
               c.name as cohortName, p.name as programName, c.startDate as cohortStartDate,
               c.tuitionAmount as tuitionAmount, l.loaneeLoanDetail.initialDeposit as initialDeposit,
               l.loaneeLoanDetail.amountRequested as loanAmountRequested, l.userIdentity.image as loaneeImage
        from LoanReferralEntity lre
        join LoaneeEntity l on lre.loaneeEntity.id = l.id
        join CohortEntity c on l.cohortId = c.id
        join ProgramEntity p on c.programId = p.id
        join OrganizationEntity o on p.organizationIdentity.id = o.id
        where lre.id = :id
    """)
    Optional<LoanReferralProjection> findLoanReferralById(@Param("id") String id);
    List<LoanReferralEntity> findAllByLoaneeEntityUserIdentityId(String userId);
}
