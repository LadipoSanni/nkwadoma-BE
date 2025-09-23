package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

public interface LoanRequestUseCase {
    Page<LoanRequest> viewAllLoanRequests(LoanRequest loanRequest, String userId) throws MeedlException;
//    Page<LoanRequest> viewAllLoanRequestsByOrganizationId(LoanRequest loanRequest) throws MeedlException;
    LoanRequest respondToLoanRequest(LoanRequest loanRequest) throws MeedlException;
    LoanRequest viewLoanRequestById(LoanRequest loanRequest, String userId) throws MeedlException;


    Page<LoanRequest> searchLoanRequest(LoanRequest loanRequest) throws MeedlException;
}
