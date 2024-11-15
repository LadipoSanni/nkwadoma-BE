package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import org.springframework.stereotype.*;

@Component
public class LoanRequestAdapter implements LoanRequestOutputPort {
    @Override
    public LoanRequest save(LoanRequest loanRequest) {
        return null;
    }
}
