package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoaneeEmailUsecase {

    void referLoaneeEmail(Loanee loanee) throws MeedlException;

    void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException;

    void sendLoanRequestApprovalEmail(LoanRequest loanRequest) throws MeedlException;
}
