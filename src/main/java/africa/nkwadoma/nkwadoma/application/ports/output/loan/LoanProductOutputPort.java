package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanProductException;

public interface LoanProductOutputPort {

//    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MiddlException;

    LoanProduct save(LoanProduct loanProduct);
}
