package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

import java.util.*;

public interface LoaneeOutputPort {
    Loanee save(Loanee loanee) throws MeedlException;

    void deleteLoanee(String loaneeId) throws MeedlException;

    Loanee findByLoaneeEmail(String email) throws MeedlException;

    List<Loanee> findAllLoaneesByCohortId(Cohort foundCohort);

    Optional<Loanee> findByUserId(String userId);
}
