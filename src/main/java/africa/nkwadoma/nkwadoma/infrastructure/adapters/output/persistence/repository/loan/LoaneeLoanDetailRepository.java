package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoaneeLoanDetailRepository extends JpaRepository<LoaneeLoanDetailEntity, String> {

    @Query("""
     SELECT loaneeLoanDetail
          from LoaneeLoanDetailEntity loaneeLoanDetail
          join CohortLoaneeEntity cohortLoanee
          where cohortLoanee.id = :cohortLoaneeId
     """)
    LoaneeLoanDetailEntity findByCohortLoaneeId(@Param("cohortLoaneeId") String cohortLoaneeId);
}
