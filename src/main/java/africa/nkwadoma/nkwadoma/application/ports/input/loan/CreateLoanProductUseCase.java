package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;

public interface CreateLoanProductUseCase {
    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MiddlException;

    void deleteLoanProductById(LoanProduct loanProduct ) throws MiddlException;

}
