package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;

public interface LoaneeLoanAggregateOutputPort {
    LoaneeLoanAggregate save(LoaneeLoanAggregate loaneeLoanAggregate);

    void delete(String loaneeLoanAggregateId);
}
