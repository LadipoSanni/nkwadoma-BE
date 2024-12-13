package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.Page;

public interface LoanOfferUseCase {
    LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException;

    Page<LoanOffer> viewAllLoanOffers(String userId,int pageSize , int pageNumber) throws MeedlException;
}
