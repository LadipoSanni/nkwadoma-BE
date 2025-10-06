package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import org.springframework.data.domain.Page;

public interface ViewLoanProductUseCase {
    LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException;

    Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct);

    Page<LoanProduct> search(String loanProductName, int pageSize, int pageNumber) throws MeedlException;

}
