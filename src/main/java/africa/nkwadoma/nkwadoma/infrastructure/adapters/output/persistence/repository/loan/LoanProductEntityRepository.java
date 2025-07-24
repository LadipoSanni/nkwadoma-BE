package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanProductEntityRepository extends JpaRepository<LoanProductEntity,String> {
    boolean existsByName(String name);

    Optional<LoanProductEntity> findByName(String name);

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
}
