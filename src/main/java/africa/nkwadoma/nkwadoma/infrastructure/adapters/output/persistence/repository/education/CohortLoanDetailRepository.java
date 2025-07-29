package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoanDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CohortLoanDetailRepository extends JpaRepository<CohortLoanDetailEntity, String> {

    CohortLoanDetailEntity findByCohortId(String cohortId);


    @Modifying
    @Query("DELETE FROM CohortLoanDetailEntity cld WHERE cld.cohort.programId = :id")
    void deleteAllByProgramId(String id);

    void deleteByCohortId(String id);

}
