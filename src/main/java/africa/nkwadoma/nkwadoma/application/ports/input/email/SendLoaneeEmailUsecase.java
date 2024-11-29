package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;

public interface SendLoaneeEmailUsecase {

    void referLoaneeEmail(LoanReferral loanReferral) throws MeedlException;

    void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException;
}
