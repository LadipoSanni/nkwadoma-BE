package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanMetricsStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;

public interface LoanMetricsUseCase {
    LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException;

    Page<LoanLifeCycle> searchLoan( String programId,String organizationId,LoanMetricsStatus status,String name,
                                    int pageSize, int pageNumber) throws MeedlException;
}

