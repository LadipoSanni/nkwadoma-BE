package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

public interface LoanRequestUseCase {
    LoanRequest createLoanRequest(LoanRequest loanRequest) throws MeedlException;
    Page<LoanRequest> viewAllLoanRequests(LoanRequest loanRequest) throws MeedlException;
//    LoanRequest viewLoanRequestById(String loanRequestId) throws MeedlException;
}
