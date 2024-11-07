package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;

public interface CohortLoanDetailsOutputPort {
    CohortLoanDetail findByCohort(String id);

    CohortLoanDetail saveCohortLoanDetails(Cohort cohort, String id) throws MeedlException;
}
