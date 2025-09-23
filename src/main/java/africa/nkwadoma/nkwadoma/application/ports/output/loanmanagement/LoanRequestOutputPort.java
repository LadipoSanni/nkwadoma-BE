package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

import java.util.*;

public interface LoanRequestOutputPort {
    LoanRequest save(LoanRequest loanRequest) throws MeedlException;

    LoanRequest findById(String loanRequestId) throws MeedlException;

    Optional<LoanRequest> findLoanRequestById(String loanRequestId) throws MeedlException;
    void deleteLoanRequestById(String id) throws MeedlException;
    Page<LoanRequest> viewAll(int pageNumber, int pageSize) throws MeedlException;
    Page<LoanRequest> viewAll(String organizationId, int pageNumber, int pageSize) throws MeedlException;

    Page<LoanRequest> searchLoanRequest(LoanRequest loanRequest) throws MeedlException;

    Page<LoanRequest> filterLoanRequestByProgram(String programId, String organizationId, int pageSize, int pageNumber) throws MeedlException;

    LoanRequest findLoanRequestByLoaneeId(String id) throws MeedlException;

    int getCountOfAllVerifiedLoanRequestInOrganization(String id);


    Page<LoanRequest> viewAllLoanRequestForLoanee(String userId, int pageNumber, int pageSize) throws MeedlException;

    LoanRequest findByCohortLoaneeId(String id) throws MeedlException;
}
