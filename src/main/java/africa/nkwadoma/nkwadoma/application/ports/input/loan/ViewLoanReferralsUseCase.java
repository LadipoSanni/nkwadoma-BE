package africa.nkwadoma.nkwadoma.application.ports.input.loan;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.data.domain.*;

public interface ViewLoanReferralsUseCase {
    Page<LoanReferral> viewLoanReferrals(LoanReferral loanReferral) throws MeedlException;
}
