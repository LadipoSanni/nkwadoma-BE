package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;

public interface SendLoaneeEmailUsecase {

    void sendReferLoaneeEmail(Loanee loanee) throws MeedlException;

    void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException;
}
