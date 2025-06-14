package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;

public interface AsynchronousLoanBookProcessingUseCase {
    @Async
    void upLoadUserData(LoanBook loanBook) throws MeedlException;

}
