package africa.nkwadoma.nkwadoma.application.ports.input.notification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;

public interface LoaneeEmailUsecase {

    void referLoaneeEmail(String loanReferralId , Loanee loanee) throws MeedlException;
    void inviteLoaneeEmail(String loanOfferId , Loanee loanee) throws MeedlException;

    void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException;

    void sendLoanRequestApprovalEmail(LoanRequest loanRequest) throws MeedlException;

}
