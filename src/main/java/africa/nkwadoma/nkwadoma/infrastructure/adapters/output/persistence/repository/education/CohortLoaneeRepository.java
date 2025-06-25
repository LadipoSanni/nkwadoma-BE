package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education;

import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CohortLoaneeRepository extends JpaRepository<CohortLoaneeEntity, String> {
    CohortLoaneeEntity findCohortLoaneeEntityByLoanee_IdAndCohort_Id(String loaneeId, String cohortId);

    CohortLoaneeEntity findCohortLoaneeEntityByCohort_ProgramIdAndLoanee_Id(String cohortProgramId, String loaneeId);

}
