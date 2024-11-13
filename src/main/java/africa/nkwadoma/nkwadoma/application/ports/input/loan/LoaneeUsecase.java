package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoaneeUseCase {
    Loanee addLoaneeToCohort(Loanee loanee) throws MeedlException;

}
