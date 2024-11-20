package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import org.springframework.stereotype.Component;

@Component
public class LoanOfferAdapter implements LoanOfferOutputPort {

    @Override
    public LoanOffer save(LoanOffer loanOffer){
        return loanOffer;
    }
    @Override
    public LoanOffer findLoanOfferById(String loanOfferId){
        return null;
    }
}
