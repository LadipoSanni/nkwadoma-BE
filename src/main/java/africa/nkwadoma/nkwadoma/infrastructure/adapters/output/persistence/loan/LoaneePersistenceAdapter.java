package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loan;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class LoaneePersistenceAdapter implements LoaneeOutputPort {

    private final LoaneeMapper loaneeMapper;
    private final LoaneeRepository loaneeRepository;
    private final IdentityManagerOutputPort identityManagerOutputPort;

    @Override
    public Loanee save(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        loanee.validate();
        LoaneeEntity loaneeEntity =
                loaneeMapper.toLoaneeEntity(loanee);
        loaneeEntity = loaneeRepository.save(loaneeEntity);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

    @Override
    public void deleteLoanee(String loaneeId) {
        loaneeRepository.deleteById(loaneeId);
    }

    @Override
    public Loanee findByLoaneeEmail(String email) throws MeedlException {
        Optional<UserIdentity> userIdentity = identityManagerOutputPort.getUserByEmail(email);
        LoaneeEntity loaneeEntity = loaneeRepository.findByLoaneeEmail(email);
        return loaneeMapper.toLoanee(loaneeEntity);
    }


}
