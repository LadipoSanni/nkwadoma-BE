package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;

public interface LoanOutputPort {
    Loan save(Loan loan) throws MeedlException;

    void deleteById(String savedLoanId);

    Loan findLoanById(String id) throws MeedlException;
}
