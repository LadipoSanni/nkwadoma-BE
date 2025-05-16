package africa.nkwadoma.nkwadoma.domain.service.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.loanBook.LoanBookUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.LoanBookOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanBookService implements LoanBookUseCase {
    private final LoanBookOutputPort loanBookOutputPort;
    @Override
    public LoanBook upLoadFile(LoanBook loanBook) throws MeedlException {
    log.info("Loan book service. Upload file started");
    return loanBookOutputPort.upLoadFile(loanBook);
    }
}
