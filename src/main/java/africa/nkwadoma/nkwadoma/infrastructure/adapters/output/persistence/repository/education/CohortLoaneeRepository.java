package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CohortLoaneeRepository extends JpaRepository<CohortLoaneeEntity, String> {
    CohortLoaneeEntity findCohortLoaneeEntityByLoanee_IdAndCohort_Id(String loaneeId, String cohortId);

    CohortLoaneeEntity findCohortLoaneeEntityByCohort_ProgramIdAndLoanee_Id(String cohortProgramId, String loaneeId);

    @Query("""
        SELECT cohort_loanee FROM CohortLoaneeEntity cohort_loanee 
            WHERE cohort_loanee.cohort.id = :cohortId AND cohort_loanee.loanee.id  IN :loaneeIds
    """)
    List<CohortLoaneeEntity> findAllCohortLoaneeEntityBy_CohortIdAnd_ListOfLoaneeId(@Param("cohortId") String cohortId,
                                                                                    @Param("loaneeIds") List<String> loaneeIds);

    Long countByLoaneeId(String loaneeId);
}
