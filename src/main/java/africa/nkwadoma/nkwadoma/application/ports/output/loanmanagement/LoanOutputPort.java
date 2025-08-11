package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import org.springframework.data.domain.*;

import java.util.*;

public interface LoanOutputPort {
    Loan save(Loan loan) throws MeedlException;

    void deleteById(String savedLoanId) throws MeedlException;

    Optional<Loan> viewLoanById(String loanId) throws MeedlException;
    Optional<Loan> findLoanByLoanOfferId(String loanOfferId) throws MeedlException;
    Loan findLoanById(String id) throws MeedlException;
    Page<Loan> findAllByOrganizationId(String organizationId, int pageSize, int pageNumber) throws MeedlException;

    Page<Loan> searchLoan(String programId, String organizationId, String name, int pageSize, int pageNumber) throws MeedlException;

    Page<Loan> findAllLoan(int pageSize, int pageNumber) throws MeedlException;

    Page<Loan> filterLoanByProgram(String programId, String organizationId, int pageSize, int pageNumber) throws MeedlException;

    String findLoanReferal(String id) throws  MeedlException;

    Page<Loan> findAllLoanDisburedToLoanee(String id, int pageNumber, int pageSize) throws MeedlException;

    Page<Loan> findAllLoanDisburedToLoaneeByLoaneeId(String loaneeId, int pageSize, int pageNUmber) throws MeedlException;
    Page<Loan> searchLoanByOrganizationNameAndLoaneeId(Loan loan) throws MeedlException;

    Page<Loan> searchLoanByOrganizationNameAndUserId(Loan loan, String id) throws MeedlException;
}
