package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import org.springframework.data.domain.Page;

import java.util.*;

public interface LoaneeOutputPort {
    Loanee save(Loanee loanee) throws MeedlException;

    void deleteLoanee(String loaneeId) throws MeedlException;

    Loanee findByLoaneeEmail(String email) throws MeedlException;

    List<Loanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException;

    Optional<Loanee> findByUserId(String userId);

    Loanee findLoaneeById(String loaneeId) throws MeedlException;

    Page<Loanee> findAllLoaneeByCohortId(String cohortId , int pageSize , int pageNumber) throws MeedlException;

    List<Loanee> findAllLoaneesByCohortId(String id) throws MeedlException;
}
