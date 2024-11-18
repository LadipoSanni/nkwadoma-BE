package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanProduct;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.StartLoanRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.response.loan.StartLoanResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;

public interface CreateLoanProductUseCase {
    LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException;

    void deleteLoanProductById(LoanProduct loanProduct ) throws MeedlException;

    LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException;

    StartLoanResponse startLoan(StartLoanRequest request) throws MeedlException;
}
