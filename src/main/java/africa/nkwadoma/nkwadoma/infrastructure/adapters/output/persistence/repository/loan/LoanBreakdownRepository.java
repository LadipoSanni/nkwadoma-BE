package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoanBreakdownEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanBreakdownRepository extends JpaRepository<LoanBreakdownEntity,String> {
    void deleteAllByCohort(CohortEntity cohortEntity);

    List<LoanBreakdownEntity> findAllByCohortId(String id);



    @Modifying
    @Query("DELETE FROM LoanBreakdownEntity lb WHERE lb.cohort.programId = :id")
    void deleteAllLoanBreakdownByProgramId(@Param("id") String id);

    LoanBreakdownEntity findByItemName(String itemName);
}
