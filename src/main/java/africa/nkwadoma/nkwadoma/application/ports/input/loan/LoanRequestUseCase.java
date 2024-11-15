package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanRequestUseCase {
    LoanRequest createLoanRequest(LoanRequest loanRequest);
}
