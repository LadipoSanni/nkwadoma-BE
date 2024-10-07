package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;

public interface LoanProductOutputPort {

//    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MiddlException;

    LoanProduct save(LoanProduct loanProduct);
}
