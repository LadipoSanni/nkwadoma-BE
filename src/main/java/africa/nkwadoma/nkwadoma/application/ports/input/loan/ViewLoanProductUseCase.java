package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ViewLoanProductUseCase {
    LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException;

    Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct);

    List<LoanProduct> search(String loanProductName) throws MeedlException;

    Page<Loan> searchForLoan(String programId, String organizationId, String name, int pageSize, int pageNumber) throws MeedlException;
}
