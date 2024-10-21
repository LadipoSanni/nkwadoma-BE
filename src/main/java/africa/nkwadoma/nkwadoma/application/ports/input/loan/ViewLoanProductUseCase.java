package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;

public interface ViewLoanProductUseCase {
    LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException;
}
