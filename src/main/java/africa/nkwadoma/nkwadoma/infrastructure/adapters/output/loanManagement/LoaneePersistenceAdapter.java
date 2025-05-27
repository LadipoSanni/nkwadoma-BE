package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoaneeException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loan;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DeferProgramRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoaneePersistenceAdapter implements LoaneeOutputPort {
    private final LoaneeMapper loaneeMapper;
    private final LoaneeRepository loaneeRepository;


    @Override
    public Loanee save(Loanee loanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanee, LoaneeMessages.LOANEE_CANNOT_BE_EMPTY.getMessage());
        loanee.validate();
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
        return loaneeMapper.toLoanee(loaneeEntity);
    }

    @Override
    public Page<Loanee> findAllLoaneeByCohortId(String cohortId, int pageSize, int pageNumber,LoaneeStatus status) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,Sort.by(Sort.Order.desc("createdAt")));
        Page<LoaneeEntity> loaneeEntities = loaneeRepository.findAllByCohortId(cohortId,status,pageRequest);
        return loaneeEntities.map(loaneeMapper::toLoanee);
    }

    @Override
    public List<Loanee> findAllLoaneesByCohortId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());
        List<LoaneeEntity> loanees = loaneeRepository.findAllLoaneesByCohortId(id);
        return loaneeMapper.toListOfLoanee(loanees);
    }

    @Override
    public List<Loanee> searchForLoaneeInCohort(String name,String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        MeedlValidator.validateDataElement(name, LoaneeMessages.LOANEE_NAME_CANNOT_BE_EMPTY.getMessage());
        List<LoaneeEntity> loaneeEntities =
                loaneeRepository.findByCohortIdAndNameFragment(cohortId,name);
        if (loaneeEntities.isEmpty()){
            return new ArrayList<>();
        }
        return loaneeEntities.stream().map(loaneeMapper::toLoanee).toList();
    }

    @Override
    public Page<Loanee> findAllLoaneeThatBenefitedFromLoanProduct(String id,int pageSize,int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loan product id cannot empty");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoaneeProjection> loaneeProjections = loaneeRepository.findAllByLoanProductId(id,pageRequest);
        return loaneeProjections.map(loaneeMapper::mapProjecttionToLoanee);
    }

    @Override
    public Page<Loanee> searchLoaneeThatBenefitedFromLoanProduct(String id, String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loan product id cannot empty");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
        Page<LoaneeProjection> loaneeProjections = loaneeRepository.findAllByLoanProductIdAndNameFragment(id,name,pageRequest);
        if (loaneeProjections.isEmpty()){
            return new PageImpl<>(new ArrayList<>());
        }
        return loaneeProjections.map(loaneeMapper::mapProjecttionToLoanee);
    }

    @Override
    public boolean checkIfLoaneeCohortExistInOrganization(String loaneeId, String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.ORGANIZATION_ID_IS_REQUIRED.getMessage());
        return loaneeRepository.checkIfLoaneeCohortExistInOrganization(loaneeId,organizationId);
    }

    @Override
    public void archiveOrUnArchiveByIds(List<String> loaneesId, LoaneeStatus loaneeStatus) throws MeedlException {
        if (loaneesId.isEmpty()){
            throw new MeedlException(LoaneeMessages.LOANEES_ID_CANNOT_BE_EMPTY.getMessage());
        }
        loaneeRepository.updateStatusByIds(loaneesId, loaneeStatus);
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
                 .orElseThrow(()-> new LoaneeException(LoaneeMessages.LOANEE_NOT_FOUND.getMessage()));
        log.info("Loanee entity found successfully {}", loaneeEntity);
        return loaneeMapper.toLoanee(loaneeEntity);
    }

}
