package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;

public interface LoaneeLoanDetailsOutputPort {
    LoaneeLoanDetail save(LoaneeLoanDetail loaneeLoanDetail);
    void delete(String id) throws MeedlException;
}
