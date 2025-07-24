package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loanbook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.AsynchronousLoanBookProcessingUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loanbook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanBookService implements LoanBookUseCase {
    private final AsynchronousLoanBookProcessingUseCase asynchronousLoanBookProcessingUseCase;


    //    @Async
    @Override
    public LoanBook upLoadUserData(LoanBook loanBook) throws MeedlException {
        asynchronousLoanBookProcessingUseCase.upLoadUserData(loanBook);
        return loanBook;
    }


    @Override
    public void uploadRepaymentRecord(LoanBook repaymentRecordBook) throws MeedlException {
        asynchronousLoanBookProcessingUseCase.uploadRepaymentHistory(repaymentRecordBook);
    }
}
