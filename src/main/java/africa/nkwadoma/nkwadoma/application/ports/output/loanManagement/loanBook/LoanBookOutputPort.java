package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;

public interface LoanBookOutputPort {
    LoanBook upLoadFile(LoanBook loanBook) throws MeedlException;
}
