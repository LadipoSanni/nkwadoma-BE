package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;

public interface SendColleagueEmailUseCase {
    void sendColleagueEmail(String organizationName,UserIdentity userIdentity) throws MeedlException;

    void sendPortforlioManagerEmail(UserIdentity portfolioManager, LoanOffer loanOffer);
}
