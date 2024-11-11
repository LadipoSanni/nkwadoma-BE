package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loan;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoaneePersistenceAdapter implements LoaneeOutputPort {
    private final LoaneeMapper loaneeMapper;
    private final LoaneeRepository loaneeRepository;


    @Override
    public Loanee save(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        loanee.validate();
        Loanee foundLoanee = findByLoaneeEmail(loanee.getLoanee().getEmail());
        if (foundLoanee != null){
            throw new LoaneeException(LoaneeMessages.LOANEE_WITH_EMAIL_EXIST.getMessage());
        }
        LoaneeEntity loaneeEntity =
                loaneeMapper.toLoaneeEntity(loanee);
        log.info("Loanee Entity: " + loaneeEntity);
        loaneeEntity = loaneeRepository.save(loaneeEntity);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

    @Override
    public void deleteLoanee(String loaneeId) {
        loaneeRepository.deleteById(loaneeId);
    }

    @Override
    public Loanee findByLoaneeEmail(String email) {
        LoaneeEntity loaneeEntity = loaneeRepository.findByLoaneeEmail(email);
        return loaneeMapper.toLoanee(loaneeEntity);
    }
}
