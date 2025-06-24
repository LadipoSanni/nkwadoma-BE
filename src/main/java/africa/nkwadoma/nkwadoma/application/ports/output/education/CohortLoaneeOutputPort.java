package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;

public interface CohortLoaneeOutputPort {

    CohortLoanee save(CohortLoanee cohortLoanee) throws MeedlException;

    void delete(String id) throws MeedlException;
}
