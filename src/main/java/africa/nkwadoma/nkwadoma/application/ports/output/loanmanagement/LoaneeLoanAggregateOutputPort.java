package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;

public interface LoaneeLoanAggregateOutputPort {
    LoaneeLoanAggregate save(LoaneeLoanAggregate loaneeLoanAggregate) throws MeedlException;

    void delete(String loaneeLoanAggregateId) throws MeedlException;

    LoaneeLoanAggregate findByLoaneeId(String id) throws MeedlException;
}
