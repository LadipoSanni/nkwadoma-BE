package africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import org.springframework.data.domain.Page;

public interface LoanOfferOutputPort {
    LoanOffer save(LoanOffer loanOffer) throws MeedlException;


    LoanOffer findLoanOfferById(String loanOfferId) throws MeedlException;

    void deleteLoanOfferById(String loanOfferId);

    Page<LoanOffer> findAllLoanOfferedToLoaneesInOrganization(String organizationId, int pageSize , int pageNumber) throws MeedlException;

    Page<LoanOffer> findAllLoanOffer(LoanOffer loanOffer) throws MeedlException;

    Page<LoanOffer> searchLoanOffer(LoanOffer loanOffer) throws MeedlException;

    Page<LoanOffer> filterLoanOfferByProgram(LoanOffer loanOffer) throws MeedlException;

    LoanOffer findLoanOfferByLoaneeId(String loaneeId) throws MeedlException;

    Page<LoanOffer> findAllLoanOfferAssignedToLoanee(String id, int pageSize, int pageNumber) throws MeedlException;

    int countNumberOfPendingLoanOfferForCohort(String id) throws MeedlException;

    int countNumberOfPendingLoanOfferForOrganization(String id) throws MeedlException;

    LoanOffer findById(String loanOfferId) throws MeedlException;
}
