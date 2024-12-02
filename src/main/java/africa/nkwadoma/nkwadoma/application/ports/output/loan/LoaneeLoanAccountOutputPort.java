package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;

public interface LoaneeLoanAccountOutputPort {
    LoaneeLoanAccount save(LoaneeLoanAccount loaneeLoanAccount) throws MeedlException;
}
