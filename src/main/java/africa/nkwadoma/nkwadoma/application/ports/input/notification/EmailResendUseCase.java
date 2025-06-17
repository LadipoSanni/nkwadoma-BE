package africa.nkwadoma.nkwadoma.application.ports.input.notification;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public interface EmailResendUseCase {
    void resendReferralEmail(String loaneeEmail) throws MeedlException;
}
