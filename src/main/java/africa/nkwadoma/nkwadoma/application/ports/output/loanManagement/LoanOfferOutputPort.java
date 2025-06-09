package africa.nkwadoma.nkwadoma.application.ports.output.loanManagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import org.springframework.data.domain.Page;

public interface LoanOfferOutputPort {
    LoanOffer save(LoanOffer loanOffer) throws MeedlException;


    LoanOffer findLoanOfferById(String loanOfferId) throws MeedlException;

    void deleteLoanOfferById(String loanOfferId);

    Page<LoanOffer> findLoanOfferInOrganization(String organization,int pageSize , int pageNumber) throws MeedlException;

    Page<LoanOffer> findAllLoanOffers(int pageSize, int pageNumber) throws MeedlException;

    Page<LoanOffer> searchLoanOffer(LoanOffer loanOffer) throws MeedlException;

    Page<LoanOffer> filterLoanOfferByProgram(LoanOffer loanOffer) throws MeedlException;

    LoanOffer findLoanOfferByLoaneeId(String loaneeId) throws MeedlException;
}
