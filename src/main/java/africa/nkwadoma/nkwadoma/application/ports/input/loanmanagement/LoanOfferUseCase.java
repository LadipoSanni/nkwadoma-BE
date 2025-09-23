package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanOfferStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.Page;

public interface LoanOfferUseCase {
    LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException;
    Page<LoanOffer> viewAllLoanOffers(LoanOffer loanOffer) throws MeedlException;
    LoanOffer viewLoanOfferDetails(String actorId, String loanOfferId) throws MeedlException;
    LoaneeLoanAccount acceptLoanOffer(LoanOffer loanOffer, OnboardingMode onboardingMode) throws MeedlException;

    Page<LoanOffer> viewAllLoanOffersInOrganization(String organizationId, int pageSize, int pageNumber) throws MeedlException;

    Page<LoanDetail> filterLoanByProgram(LoanOffer loanOffer) throws MeedlException;

    LoanOffer withdrawLoanOffer(String loanOfferId, LoanOfferStatus loanOfferStatus) throws MeedlException;

    Page<LoanOffer> searchLoanOffer(LoanOffer loanOffer) throws MeedlException;
}
