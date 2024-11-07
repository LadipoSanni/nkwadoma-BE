package africa.nkwadoma.nkwadoma.application.ports.output.education;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;

public interface LoanDetailsOutputPort {
    LoanDetail saveLoanDetails(LoanDetail loanDetail) throws MeedlException;
}
