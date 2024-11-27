package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanRequestUseCase {
    LoanRequest createLoanRequest(LoanRequest loanRequest) throws MeedlException;
    LoanRequest respondToLoanRequest(LoanRequest loanRequest) throws MeedlException;
}
