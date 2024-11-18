package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;

public interface LoanOfferOutputPort {
    LoanOffer save(LoanOffer loanOffer);
    LoanOffer findLoanOfferById(String loanOfferId);
}
