package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanAdapter implements LoanOutputPort {
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    @Override
    public Loan save(Loan loan) throws MeedlException {
        LoanEntity loanEntity  = loanMapper.mapToLoanEntity(loan);
        loanEntity = loanRepository.save(loanEntity);
        return loanMapper.mapToLoan(loanEntity);
    }
}
