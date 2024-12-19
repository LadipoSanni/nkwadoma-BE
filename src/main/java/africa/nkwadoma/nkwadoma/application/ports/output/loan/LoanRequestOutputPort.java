package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface LoanRequestOutputPort {
    LoanRequest save(LoanRequest loanRequest) throws MeedlException;

    Optional<LoanRequest> findById(String loanRequestId) throws MeedlException;

    void deleteLoanRequestById(String id);
    Page<LoanRequest> viewAll(int pageNumber, int pageSize) throws MeedlException;
}
