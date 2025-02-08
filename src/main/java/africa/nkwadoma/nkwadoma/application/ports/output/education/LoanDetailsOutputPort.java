package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;

public interface LoanDetailsOutputPort {
    LoanDetail saveLoanDetails(LoanDetail loanDetail) throws MeedlException;
}
