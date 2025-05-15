package africa.nkwadoma.nkwadoma.application.ports.input.loanManagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import org.springframework.data.domain.*;

public interface CreateLoanProductUseCase {
    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException;

    void deleteLoanProductById(LoanProduct loanProduct ) throws MeedlException;

    LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException;

    Loan startLoan(Loan loan) throws MeedlException;
    Page<Loan> viewAllLoansByOrganizationId(Loan loan) throws MeedlException;
    Loan viewLoanDetails(String loanId) throws MeedlException;

    Page<Loan> viewAllLoans(int pageSize, int pageNumber) throws MeedlException;
}
