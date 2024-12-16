package africa.nkwadoma.nkwadoma.application.ports.output.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import org.springframework.data.domain.Page;

public interface LoanOfferOutputPort {
    LoanOffer save(LoanOffer loanOffer) throws MeedlException;
    LoanOffer findLoanOfferById(String loanOfferId);

    void deleteLoanOfferById(String loanOfferId);

    Page<LoanOffer> findLoanOfferInOrganization(String organization,int pageSize , int pageNumber) throws MeedlException;

    Page<LoanOffer> findAllLoanOffers(int pageSize, int pageNumber) throws MeedlException;
}
