package africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.Page;

public interface ViewLoanReferralsUseCase {
    LoanReferral viewLoanReferral(String actorId, String loanReferralId) throws MeedlException;

    Page<LoanReferral> viewLoanReferralForLoanee(String userId, int pageNumber, int pageSize) throws MeedlException;

}
