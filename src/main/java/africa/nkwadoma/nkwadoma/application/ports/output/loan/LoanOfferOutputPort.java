package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;

public interface LoanOfferOutputPort {
    LoanOffer save(LoanOffer loanOffer) throws MeedlException;
    LoanOffer findLoanOfferById(String loanOfferId);

    void deleteLoanOfferById(String loanOfferId);
}
