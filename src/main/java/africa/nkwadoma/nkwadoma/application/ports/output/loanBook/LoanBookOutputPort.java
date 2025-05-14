package africa.nkwadoma.nkwadoma.application.ports.output.loanBook;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanBook;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanBookOutputPort {
    LoanBook upLoadFile(LoanBook loanBook);

    Page<LoanBook> search(String loanBookName);
}
