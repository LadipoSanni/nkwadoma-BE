package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanDetailSummary;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanSummaryProjection;
import org.springframework.data.domain.Page;

public interface LoaneeLoanAggregateOutputPort {
    LoaneeLoanAggregate save(LoaneeLoanAggregate loaneeLoanAggregate) throws MeedlException;

    void delete(String loaneeLoanAggregateId) throws MeedlException;

    LoaneeLoanAggregate findByLoaneeId(String id) throws MeedlException;

    Page<LoaneeLoanAggregate> findAllLoanAggregate(int pageSize, int pageNumber);

    Page<LoaneeLoanAggregate> searchLoanAggregate(String name, int pageSize, int pageNumber);

    LoanDetailSummary getLoanAggregationSummary();
}
