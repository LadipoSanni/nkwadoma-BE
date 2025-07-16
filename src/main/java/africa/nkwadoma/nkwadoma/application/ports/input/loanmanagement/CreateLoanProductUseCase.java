package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanResponseWrapper;
import org.springframework.data.domain.*;

public interface CreateLoanProductUseCase {
    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException;

    void deleteLoanProductById(LoanProduct loanProduct ) throws MeedlException;

    LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException;

}
