package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;

public interface LoanOfferUseCase {


    LoanOffer createLoanOffer(String loanRequestId) throws MeedlException;
}
