package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface LoanRequestRepository extends JpaRepository<LoanRequestEntity, String> {

    @Query("select new africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest" +
            "(lr.id, l.userIdentity.firstName, l.userIdentity.lastName, o.name, lr.loanAmountRequested, lr.createdDate," +
            " l.loaneeLoanDetail.initialDeposit, c.startDate, p.name)" +
            "from LoanRequestEntity lr " +
            "join LoaneeEntity l on lr.loaneeEntity.id = l.id " +
            "join CohortEntity c on l.cohortId = c.id " +
            "join ProgramEntity p on c.programId = p.id " +
            "join OrganizationEntity o on p.organizationEntity.id = o.id")
    Page<LoanRequest> findAllLoanRequests(Pageable pageable);
}
