package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import org.springframework.scheduling.annotation.Async;

public interface AsynchronousLoanBookProcessingUseCase {
//    @Async
    void upLoadUserData(LoanBook loanBook) throws MeedlException;

}
