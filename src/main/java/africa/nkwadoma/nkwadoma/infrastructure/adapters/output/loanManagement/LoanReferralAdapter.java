package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class LoanReferralAdapter implements LoanReferralOutputPort {
    private final LoanReferralRepository loanReferralRepository;

    @Override
    public LoanReferral saveLoanReferral(LoanReferral loanReferral) {
        return null;
    }
}
