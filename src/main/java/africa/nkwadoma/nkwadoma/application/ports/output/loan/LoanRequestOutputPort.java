package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanRequestOutputPort {
    LoanRequest save(LoanRequest loanRequest) throws MeedlException;
}
