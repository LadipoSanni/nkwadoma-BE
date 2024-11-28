package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoanOfferUseCase {
    LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException;
}
