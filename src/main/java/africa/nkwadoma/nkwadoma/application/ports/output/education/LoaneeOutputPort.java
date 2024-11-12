package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;

public interface LoaneeOutputPort {
    Loanee save(Loanee loanee) throws MeedlException;

    void deleteLoanee(String loaneeId) throws MeedlException;

    Loanee findByLoaneeEmail(String email);
}
