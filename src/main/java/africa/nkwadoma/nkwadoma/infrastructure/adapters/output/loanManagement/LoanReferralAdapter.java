package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
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
        log.info("Saving loan referral: {}", loanReferral);
        MeedlValidator.validateObjectInstance(loanReferral);
        LoanReferralEntity loanReferralEntity = loanReferralMapper.toLoanReferralEntity(loanReferral);
        LoanReferralEntity savedLoanReferralEntity = loanReferralRepository.save(loanReferralEntity);
        log.info("Loan Referral saved successfully: {}", savedLoanReferralEntity);
        return loanReferralMapper.toLoanReferral(savedLoanReferralEntity);
    }

    @Override
    public Optional<LoanReferral> findLoanReferralById(String loanReferralId) throws MeedlException {
        MeedlValidator.validateDataElement(loanReferralId);
        loanReferralId = loanReferralId.trim();
        MeedlValidator.validateUUID(loanReferralId);
        Optional<LoanReferralEntity> loanReferralEntity = loanReferralRepository.findById(loanReferralId);
        if (loanReferralEntity.isEmpty()) {
            return Optional.empty();
        }
        log.info("Found loan referral entity: {}", loanReferralEntity.get());
        return Optional.of(loanReferralMapper.toLoanReferral(loanReferralEntity.get()));
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
}
