package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;

public interface LoaneeUseCase {

    Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException;

    Loanee viewLoaneeDetails(String loaneeId) throws MeedlException;
}