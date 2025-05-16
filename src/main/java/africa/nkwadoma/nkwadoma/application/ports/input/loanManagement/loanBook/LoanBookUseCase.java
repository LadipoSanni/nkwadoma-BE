package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;

public interface LoanBookUseCase {
    LoanBook upLoadFile(LoanBook loanBook) throws MeedlException;
}
