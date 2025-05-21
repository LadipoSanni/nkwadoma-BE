package africa.nkwadoma.nkwadoma.application.ports.input.email;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;

public interface EmailResendUseCase {
    void resendReferralEmail(String loaneeEmail) throws MeedlException;
}
