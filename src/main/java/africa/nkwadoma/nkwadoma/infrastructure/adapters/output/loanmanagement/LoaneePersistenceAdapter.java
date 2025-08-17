package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoaneePersistenceAdapter implements LoaneeOutputPort {
    private final LoaneeMapper loaneeMapper;
    private final LoaneeRepository loaneeRepository;


    @Override
    public Loanee save(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        loanee.validateForSaving();
        log.info("Loanee value's to save before mapping {}", loanee);
        LoaneeEntity loaneeEntity =
                loaneeMapper.toLoaneeEntity(loanee);
        log.info("Loanee Entity: " + loaneeEntity);
        loaneeEntity = loaneeRepository.save(loaneeEntity);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

    @Override
    public void deleteLoanee(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Optional<LoaneeEntity> loaneeEntity = loaneeRepository.findById(loaneeId);
        if (loaneeEntity.isPresent()) {
            log.info("Found loanee: {}", loaneeEntity.get());
            loaneeRepository.deleteById(loaneeEntity.get().getId());
        }
    }

    @Override
    public Loanee findByLoaneeEmail(String email) throws MeedlException {
        MeedlValidator.validateEmail(email);
        LoaneeEntity loaneeEntity = loaneeRepository.findLoaneeByUserIdentityEmail(email);
        log.info("Found loanee from db : {}", loaneeEntity);
        Loanee loanee =  loaneeMapper.toLoanee(loaneeEntity);
        log.info("Mapped loanee: {}", loanee);
        return loanee;
    }

    @Override
    public List<Loanee> findAllLoaneesByCohortId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        List<LoaneeEntity> loanees = loaneeRepository.findAllLoaneesByCohortId(id);
        return loaneeMapper.toListOfLoanee(loanees);
    }

    @Override
    public Page<Loanee> searchForLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getCohortId(),CohortMessages.INVALID_COHORT_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,Sort.by(Sort.Order.desc("createdAt")));
        log.debug("Searching for loanees with params: cohortId={}, nameFragment={}, status={}, uploadedStatus={}",
                loanee.getCohortId(), loanee.getLoaneeName(), loanee.getLoaneeStatus(), loanee.getUploadedStatus());
        Page<LoaneeEntity> loaneeEntities =
                loaneeRepository.findByCohortIdAndNameFragment(loanee.getCohortId(),
                        loanee.getLoaneeName(),loanee.getLoaneeStatus(), loanee.getUploadedStatus(),pageRequest);
        if (loaneeEntities.isEmpty()){
            return Page.empty();
        }
        return loaneeEntities.map(loaneeMapper::toLoanee);
    }


    @Override
    public boolean checkIfLoaneeCohortExistInOrganization(String loaneeId, String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.ORGANIZATION_ID_IS_REQUIRED.getMessage());
        return loaneeRepository.checkIfLoaneeCohortExistInOrganization(loaneeId,organizationId);
    }

    @Override
    public Page<Loanee> findAllLoanee(int pageSize, int pageNumber) throws MeedlException {
        log.info("pageSize = {}, pageNumber = {}", pageSize, pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoaneeEntity> loaneeEntities =  loaneeRepository.findAll(pageRequest);
        return loaneeEntities.map(loaneeMapper::toLoanee);
    }

    @Override
    public List<Loanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        List<LoaneeEntity> loanees = loaneeRepository.findAllLoaneesByCohortIdAndLoaneeIds(id,loaneeIds);
        return loaneeMapper.toListOfLoanee(loanees);
    }


    @Override
    public Optional<Loanee> findByUserId(String userId) throws MeedlException {
        MeedlValidator.validateUUID(userId, UserMessages.INVALID_USER_ID.getMessage());
        Optional<LoaneeEntity> loaneeEntity = loaneeRepository.findLoaneeByUserIdentityId(userId);
        if (loaneeEntity.isEmpty()) {
            return Optional.empty();
        }
        log.info("Loanee found by user id. Is identity verified field Before mapping {}", loaneeEntity.get().getUserIdentity().isIdentityVerified());
        Loanee loanee = loaneeMapper.toLoanee(loaneeEntity.get());
        loanee.getUserIdentity().setIdentityVerified(loaneeEntity.get().getUserIdentity().isIdentityVerified());
        log.info("Loanee found by user id. Is identity verified field after mapping {}", loaneeEntity.get().getUserIdentity().isIdentityVerified());
        return Optional.of(loanee);
    }

    @Override
    public Loanee findLoaneeById(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        LoaneeEntity loaneeEntity = loaneeRepository.findById(loaneeId)
                 .orElseThrow(()-> new LoanException(LoaneeMessages.LOANEE_NOT_FOUND.getMessage()));
        log.info("Loanee entity found successfully {}", loaneeEntity);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

}
