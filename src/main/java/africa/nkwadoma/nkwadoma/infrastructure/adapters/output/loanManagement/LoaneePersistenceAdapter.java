package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
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
        log.info("Loanee Entity: " + loaneeEntity);
        loaneeEntity = loaneeRepository.save(loaneeEntity);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

    @Override
    public void deleteLoanee(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId);
        Optional<LoaneeEntity> loaneeEntity = loaneeRepository.findById(loaneeId);
        if (loaneeEntity.isPresent()) {
            log.info("Found loanee: {}", loaneeEntity.get());
            loaneeRepository.deleteById(loaneeEntity.get().getId());
        }
    }

    @Override
    public Loanee findByLoaneeEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        Optional<UserIdentity> userIdentity = identityManagerOutputPort.getUserByEmail(email);
        LoaneeEntity loaneeEntity = loaneeRepository.findByLoaneeEmail(email);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

    @Override
    public List<Loanee> findAllLoaneesByCohortId(Cohort foundCohort) {
        List<LoaneeEntity> loaneeEntities = loaneeRepository.findAllByCohortId(foundCohort.getId());
       return loaneeMapper.toListOfLoanee(loaneeEntities);
    }

    @Override
    public Optional<Loanee> findByUserId(String userId) {
        Optional<LoaneeEntity> loaneeEntity = loaneeRepository.findByLoaneeId(userId);
        if (loaneeEntity.isEmpty()) {
            return Optional.empty();
        }
        Loanee loanee = loaneeMapper.toLoanee(loaneeEntity.get());
        return Optional.of(loanee);
    }

}
