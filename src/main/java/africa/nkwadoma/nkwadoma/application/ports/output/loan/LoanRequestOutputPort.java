package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

public interface LoanRequestOutputPort {
    LoanRequest save(LoanRequest loanRequest) throws MeedlException;
    Page<LoanRequest> viewAll(int pageNumber, int pageSize) throws MeedlException;
}
