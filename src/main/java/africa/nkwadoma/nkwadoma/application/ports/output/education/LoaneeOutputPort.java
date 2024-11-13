package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;

import java.util.List;

public interface LoaneeOutputPort {


    Loanee save(Loanee loanee) throws MeedlException;

    void deleteLoanee(String loaneeId);

    Loanee findByLoaneeEmail(String email) throws MeedlException;


    List<Loanee> findAllLoaneeByCohortId(String cohortId);
}
