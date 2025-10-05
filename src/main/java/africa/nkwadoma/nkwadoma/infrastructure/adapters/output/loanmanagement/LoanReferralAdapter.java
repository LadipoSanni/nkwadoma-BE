package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.identity.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanReferralAdapter implements LoanReferralOutputPort {
    private final LoanReferralRepository loanReferralRepository;
    private final LoanReferralMapper loanReferralMapper;

    @Override
    public LoanReferral save(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral, "Loan referral cannot be empty.");
        loanReferral.validateForCreate();
        LoanReferralEntity loanReferralEntity = loanReferralMapper.toLoanReferralEntity(loanReferral);
        LoanReferralEntity savedLoanReferralEntity = loanReferralRepository.save(loanReferralEntity);
        return loanReferralMapper.toLoanReferral(savedLoanReferralEntity);
    }

    @Override
    public Optional<LoanReferral> findLoanReferralById(String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(loanReferralId, "Please provide a valid loan referral identification.");
        Optional<LoanReferralProjection> loanReferralProjection = loanReferralRepository.findLoanReferralById(loanReferralId);
        if (loanReferralProjection.isEmpty()) {
            log.info("Empty Loan referral projection: {}", loanReferralProjection);
            return Optional.empty();
        }
        LoanReferral loanReferral = loanReferralMapper.mapProjectionToLoanReferralEntity(loanReferralProjection.get());
        log.info("Mapped LoanReferral: {}", loanReferral);
        return Optional.of(loanReferral);
    }

    @Override
    public LoanReferral findById(String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(loanReferralId, LoanMessages.LOAN_REFERRAL_ID_MUST_NOT_BE_EMPTY.getMessage());
        LoanReferralEntity loanReferralEntity = loanReferralRepository
                .findById(loanReferralId).orElseThrow(()-> new LoanException("Loan referral not found"));
        log.info("before mapping loan referral entity : loanee verification is   {}", loanReferralEntity.getCohortLoanee().getLoanee().getUserIdentity().isIdentityVerified());
        return loanReferralMapper.toLoanReferral(loanReferralEntity);
    }

    @Override
    public void deleteLoanReferral(String loanReferralId) throws MeedlException {
        MeedlValidator.validateUUID(loanReferralId, "Please provide a valid loan referral identification.");
        Optional<LoanReferralEntity> loanReferralEntity = loanReferralRepository.findById(loanReferralId);
        if (loanReferralEntity.isPresent()) {
            log.info("Found loan referral: {}", loanReferralEntity.get());
            loanReferralRepository.delete(loanReferralEntity.get());
        }
        else log.info("Loan referral not found");
    }

    @Override
    public List<LoanReferral> findLoanReferralByUserId(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        List<LoanReferralEntity> loanReferralEntities = loanReferralRepository.findAllByCohortLoanee_Loanee_UserIdentity_id(userId);
        return loanReferralMapper.toLoanReferrals(loanReferralEntities);
    }

    @Override
    public LoanReferral findLoanReferralByLoaneeIdAndCohortId(String id, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loanee id cannot be null or empty");
        MeedlValidator.validateUUID(cohortId,"Cohort id cannot be null or empty");
        LoanReferralEntity loanReferral =
                loanReferralRepository.findByLoaneeEntityIdAndLoaneeEntityCohortId(id,cohortId);
        return loanReferralMapper.toLoanReferral(loanReferral);
    }

    @Override
    public List<LoanReferral> viewAll() {
        List<LoanReferralEntity> loanReferrals =
                loanReferralRepository.findAll();
        log.info("All loan referral entities found {}", loanReferrals);
        return loanReferrals.stream().map(loanReferralMapper::toLoanReferral).toList();
    }

    @Override
    public LoanReferral findByEmail(String loaneeEmail) throws MeedlException {
        LoanReferralEntity loanReferral = loanReferralRepository.findAllByCohortLoanee_Loanee_UserIdentity_Email(loaneeEmail)
                .orElseThrow(()-> new MeedlException("Loan referral not found by loanee email: "+loaneeEmail));
        log.info("Loan referral entity found {}",loanReferral);
        return loanReferralMapper.toLoanReferral(loanReferral);
    }

    @Override
    public LoanReferral findLoanReferralByCohortLoaneeId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"Cohort loanee id cannot be empty");

        LoanReferralEntity loanReferralEntity = loanReferralRepository.findLoanReferralByCohortLoaneeId(id)
                .orElseThrow(() -> new LoanException("Loan referral not found for cohort loanee id: "+id));
        log.info("Loan referral entity found {}",loanReferralEntity);
        return loanReferralMapper.toLoanReferral(loanReferralEntity);
    }

    @Override
    public Page<LoanReferral> findAllLoanReferralsForLoanee(String loaneeId, int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, UserMessages.INVALID_USER_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoanReferralProjection> loanReferralEntities = loanReferralRepository.findAllLoanReferralsForLoanee(loaneeId, pageRequest);
        return loanReferralEntities.map(loanReferralMapper::mapProjectionToLoanReferralEntity);
    }

    @Override
    public List<LoanReferral> findAllLoanReferralsByUserIdAndStatus(String id, LoanReferralStatus loanReferralStatus) throws MeedlException {
        MeedlValidator.validateUUID(id,UserMessages.INVALID_USER_ID.getMessage());
        List<LoanReferralEntity> loanReferralEntities =
                loanReferralRepository.findAllByCohortLoanee_Loanee_UserIdentity_idAndLoanReferralStatus(id,loanReferralStatus);
        return loanReferralEntities.stream().map(loanReferralMapper::toLoanReferral).collect(Collectors.toList());
    }

    @Override
    public Page<LoanReferral> findAllLoanReferrals(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validatePageSize(loanReferral.getPageSize());
        MeedlValidator.validatePageNumber(loanReferral.getPageNumber());
        Pageable pageRequest = PageRequest.of(loanReferral.getPageNumber(), loanReferral.getPageSize());

        Page<LoanReferralProjection> loanReferralProjections =
                loanReferralRepository.findAllLoanReferrals(
                        loanReferral.getProgramId(),loanReferral.getOrganizationId(),pageRequest);

        return loanReferralProjections.map(loanReferralMapper::mapProjectionToLoanReferral);
    }

    @Override
    public Page<LoanReferral> searchLoanReferrals(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validatePageSize(loanReferral.getPageSize());
        MeedlValidator.validatePageNumber(loanReferral.getPageNumber());
        Pageable pageRequest = PageRequest.of(loanReferral.getPageNumber(), loanReferral.getPageSize());

        Page<LoanReferralProjection> loanReferralProjections =
                loanReferralRepository.searchLoanReferrals(
                        loanReferral.getName(),loanReferral.getProgramId(),loanReferral.getOrganizationId(),pageRequest);

        return loanReferralProjections.map(loanReferralMapper::mapProjectionToLoanReferral);
    }

    @Override
    public void deleteAll() {
        loanReferralRepository.deleteAll();
    }
}

