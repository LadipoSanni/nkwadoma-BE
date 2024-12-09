package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.LoanMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoanEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoanRepository;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanAdapter implements LoanOutputPort {
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;
    @Override
    public Loan save(Loan loan) throws MeedlException {
        MeedlValidator.validateObjectInstance(loan);
        loan.validate();
        log.info("Loan: {}", loan);
        LoanEntity loanEntity  = loanMapper.mapToLoanEntity(loan);
        loanEntity = loanRepository.save(loanEntity);
        return loanMapper.mapToLoan(loanEntity);
    }

    @Override
    public void deleteById(String loanId) throws MeedlException {
        MeedlValidator.validateUUID(loanId);
        loanRepository.deleteById(loanId);
        log.info("Loan with id {} deleted successfully",loanId);
    }

    @Override
    public Loan findLoanById(String id) throws MeedlException {
        MeedlValidator.validateUUID(id);
        LoanEntity foundLoanEntity = loanRepository.findById(id).orElseThrow(()->new LoanException("Could not find Loan"));
        return loanMapper.mapToLoan(foundLoanEntity);
    }
}
