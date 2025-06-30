package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;

public interface CohortLoanDetailOutputPort {

    CohortLoanDetail save(CohortLoanDetail cohortLoanDetail) throws MeedlException;

    void delete(String id) throws MeedlException;

    CohortLoanDetail findByCohortId(String cohortId) throws MeedlException;
}
