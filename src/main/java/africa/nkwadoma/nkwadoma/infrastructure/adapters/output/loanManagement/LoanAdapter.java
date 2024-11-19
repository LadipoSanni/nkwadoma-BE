package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;

public class LoanAdapter implements LoanOutputPort {
    @Override
    public Loan save(Loan loan) throws MeedlException {
        return null;
    }

    @Override
    public void deleteById(String savedLoanId) {

    }
}
