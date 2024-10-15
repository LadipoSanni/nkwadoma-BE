package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;

public interface CreateLoanProductUseCase {
    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException;

    void deleteLoanProductById(LoanProduct loanProduct ) throws MeedlException;

}
