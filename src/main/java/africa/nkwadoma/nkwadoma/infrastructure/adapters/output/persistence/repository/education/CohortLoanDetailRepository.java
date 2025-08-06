package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CohortLoanDetailRepository extends JpaRepository<CohortLoanDetailEntity, String> {

    CohortLoanDetailEntity findByCohortId(String cohortId);


    @Modifying
    @Query("DELETE FROM CohortLoanDetailEntity cld WHERE cld.cohort.programId = :id")
    void deleteAllByProgramId(String id);

    void deleteByCohortId(String id);

    @Query("""
    
        select cld from CohortLoanDetailEntity cld
            
           join CohortLoaneeEntity cle on cle.cohort.id = cld.cohort.id
           join LoaneeLoanDetailEntity lld on lld.id = cle.loaneeLoanDetail.id
               
            where lld.id = :id        
    """)
    CohortLoanDetailEntity findByLoaneeLoanDetailId(@Param("id") String id);
}
