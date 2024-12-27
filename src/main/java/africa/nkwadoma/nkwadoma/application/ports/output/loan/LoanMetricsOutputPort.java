package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

import java.util.*;

public interface LoanMetricsOutputPort {
    LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException;
    Optional<LoanMetrics> findTopOrganizationWithLoanRequest();
    void delete(String loanMetricsId) throws MeedlException;
}
