package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanMetricsProjection;

import java.util.*;

public interface LoanMetricsOutputPort {
    LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException;
    Optional<LoanMetrics> findTopOrganizationWithLoanRequest();
    void delete(String loanMetricsId) throws MeedlException;
     Optional<LoanMetrics> findByOrganizationId(String organizationId) throws MeedlException;

    LoanMetricsProjection calculateAllMetrics();
}
