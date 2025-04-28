package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanMetricsUseCase {
    LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException;

    LoanMetrics createLoanMetrics(String id) throws MeedlException;

    void correctLoanRequestCount() throws MeedlException;
}

