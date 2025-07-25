package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeLoanDetailEntity;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoaneeLoanDetailRepository extends JpaRepository<LoaneeLoanDetailEntity, String> {

    @Query("""
     SELECT loaneeLoanDetail
          
          from CohortLoaneeEntity cohortLoanee
          join LoaneeLoanDetailEntity loaneeLoanDetail on loaneeLoanDetail.id = cohortLoanee.loaneeLoanDetail.id
          where cohortLoanee.id = :cohortLoaneeId
     """)
    LoaneeLoanDetailEntity findByCohortLoaneeId(@Param("cohortLoaneeId") String cohortLoaneeId);

    @Query("""
    SELECT
        COALESCE(SUM(lld.amountReceived), 0) AS totalAmountReceived,
        COALESCE(SUM(lld.amountRepaid), 0) AS totalAmountRepaid,
        COALESCE(SUM(lld.amountOutstanding), 0) AS totalAmountOutstanding
    FROM LoaneeLoanDetailEntity lld
    JOIN CohortLoaneeEntity cle ON cle.loaneeLoanDetail.id = lld.id
    JOIN LoaneeEntity l ON l.id = cle.loanee.id
    JOIN UserEntity u ON u.id = l.userIdentity.id
    WHERE u.id = :userId
""")
    LoanSummaryProjection getLoanSummary(@Param("userId") String userId);


    @Query("""
     SELECT loaneeLoanDetail
    
          from CohortLoaneeEntity cohortLoanee
          join LoaneeLoanDetailEntity loaneeLoanDetail on loaneeLoanDetail.id = cohortLoanee.loaneeLoanDetail.id
          where cohortLoanee.cohort.id = :cohortId and cohortLoanee.loanee.id = :loaneeId
     """)
    LoaneeLoanDetailEntity findByCohortAndLoaneeId(@Param("cohortId") String cohortId, @Param("loaneeId")String loaneeId);

    @Query("""
    SELECT loaneeLoanDetail
    
          from CohortLoaneeEntity cohortLoanee
          join LoaneeLoanDetailEntity loaneeLoanDetail on loaneeLoanDetail.id = cohortLoanee.loaneeLoanDetail.id 
          join LoanReferralEntity lre on lre.cohortLoanee.id = cohortLoanee.id
          join LoanRequestEntity lr on lr.id = lre.id
              
          where lr.id = :id             
    """)
    LoaneeLoanDetailEntity findByLoanRequestId(@Param("id") String id);
}
