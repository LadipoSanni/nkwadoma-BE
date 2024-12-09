package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanReferralAdapter implements LoanReferralOutputPort {
    private final LoanReferralRepository loanReferralRepository;
    private final LoanReferralMapper loanReferralMapper;

    @Override
    public LoanReferral saveLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral);
        LoanReferralEntity loanReferralEntity = loanReferralMapper.toLoanReferralEntity(loanReferral);
        LoanReferralEntity savedLoanReferralEntity = loanReferralRepository.save(loanReferralEntity);
        return loanReferralMapper.toLoanReferral(savedLoanReferralEntity);
    }

    @Override
    public Optional<LoanReferral> findLoanReferralById(String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(loanReferralId);
        Optional<LoanReferralProjection> loanReferralProjection = loanReferralRepository.findLoanReferralById(loanReferralId);
        if (loanReferralProjection.isEmpty()) {
            log.info("Empty Loan referral projection: {}", loanReferralProjection);
            return Optional.empty();
        }
        log.info("LoanReferral Projection : {}", loanReferralProjection.get());
        //TODO: this projection is not retrieving the loanReferral details properly
        LoanReferral loanReferral = loanReferralMapper.mapProjectionToLoanReferralEntity(loanReferralProjection.get());
        log.info("Mapped LoanReferral : {}", loanReferral);
        return Optional.of(loanReferral);
    }

    @Override
    public void deleteLoanReferral(String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(loanReferralId);
        Optional<LoanReferralEntity> loanReferralEntity = loanReferralRepository.findById(loanReferralId);
        if (loanReferralEntity.isPresent()) {
            log.info("Found loan referral: {}", loanReferralEntity.get());
            loanReferralRepository.delete(loanReferralEntity.get());
        }
        else log.info("Loan referral not found");
    }

    @Override
    public LoanReferral createLoanReferral(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee);
        LoanReferral loanReferral = new LoanReferral();
        loanReferral.setLoanee(loanee);
        loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);
        loanReferral.validateForCreate();
        LoanReferralEntity loanReferralEntity =
                loanReferralMapper.toLoanReferralEntity(loanReferral);
        loanReferralEntity = loanReferralRepository.save(loanReferralEntity);
        return loanReferralMapper.toLoanReferral(loanReferralEntity);
    }

    @Override
    public List<LoanReferral> findLoanReferralByUserId(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId);
        List<LoanReferralEntity> loanReferralEntities = loanReferralRepository.findAllByLoaneeEntityUserIdentityId(userId);
        return loanReferralMapper.toLoanReferrals(loanReferralEntities);
    }

    @Override
    public LoanReferral findById(String loanReferralId) throws LoanException {
        LoanReferralEntity loanReferralEntity = loanReferralRepository
                .findById(loanReferralId).orElseThrow(()-> new LoanException("Loan referral not found "));
        return loanReferralMapper.toLoanReferral(loanReferralEntity);
    }
}

