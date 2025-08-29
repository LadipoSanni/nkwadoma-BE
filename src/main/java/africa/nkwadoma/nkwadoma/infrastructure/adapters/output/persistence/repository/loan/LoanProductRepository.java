package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanProductRepository extends JpaRepository<LoanProductEntity,String> {
    boolean existsByName(String name);

    Optional<LoanProductEntity> findByName(String name);
    Optional<LoanProductEntity> findByNameIgnoreCase(String name);

    Page<LoanProductEntity> findByNameContainingIgnoreCase(String loanProductName, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);


    @Query("""
    select lp from LoanProductEntity lp
        
            join LoanOfferEntity  lo on lo.loanProduct.id = lp.id
            join LoanRequestEntity lr on lr.id = lo.id
            join LoanReferralEntity  lre on lre.id = lr.id
            join CohortLoaneeEntity cle on cle.id = lre.cohortLoanee.id
             
          where cle.id = :id    
                
    """)
    LoanProductEntity findByCohortLoaneeId(@Param("id") String id);


    @Query("""
    select lo.loanProduct
        FROM LoanOfferEntity lo
        WHERE lo.id = :loanOfferId
    """)
    LoanProductEntity findByLoanOfferId(String loanOfferId);

    @Query("""
    SELECT loe.loanProduct
    FROM CohortLoaneeEntity cle
    JOIN cle.loaneeLoanDetail lld
    JOIN LoanReferralEntity lre ON lre.cohortLoanee.id = cle.id
    JOIN LoanOfferEntity loe ON loe.id = lre.id
    WHERE lld.id = :loaneeLoanDetailId
""")
    LoanProductEntity findLoanProductByLoaneeLoanDetailId(String loaneeLoanDetailId);
}
