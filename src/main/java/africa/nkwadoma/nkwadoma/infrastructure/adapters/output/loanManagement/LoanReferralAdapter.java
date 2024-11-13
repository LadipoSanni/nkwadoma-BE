package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.stereotype.*;

@Component
public class LoanReferralAdapter implements LoanReferralOutputPort {
    @Override
    public LoanReferral saveLoanReferral(LoanReferral loanReferral) {
        return null;
    }
}
