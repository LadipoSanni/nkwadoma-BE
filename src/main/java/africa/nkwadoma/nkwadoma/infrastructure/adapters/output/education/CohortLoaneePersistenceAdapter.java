package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.LoanException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.CohortLoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortLoaneeMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component
public class CohortLoaneePersistenceAdapter implements CohortLoaneeOutputPort {

    private final CohortLoaneeMapper cohortLoaneeMapper;
    private final CohortLoaneeRepository cohortLoaneeRepository;

    @Override
    public CohortLoanee save(CohortLoanee cohortLoanee) throws MeedlException {
        MeedlValidator.validateObjectInstance(cohortLoanee, CohortMessages.COHORT_LOANEE_CANNOT_BE_NULL.getMessage());
        cohortLoanee.validate();

        CohortLoaneeEntity cohortLoaneeEntity = cohortLoaneeMapper.toCohortLoaneeEntity(cohortLoanee);
        log.info("After mapping cohort loanee to entity = = : {}", cohortLoaneeEntity);
        cohortLoaneeEntity = cohortLoaneeRepository.save(cohortLoaneeEntity);
        log.info("After saving cohort loanee = = : {}", cohortLoaneeEntity);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }


    @Override
    public void delete(String id) throws MeedlException {
        log.info("Deleting cohort loanee = : {}", id);
        MeedlValidator.validateUUID(id,CohortMessages.INVALID_COHORT_LOANEE_ID.getMessage());
        cohortLoaneeRepository.deleteById(id);
    }

    @Override
    public CohortLoanee findCohortLoaneeByLoaneeIdAndCohortId(String loaneeId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());

        CohortLoaneeProjection cohortLoaneeProjection =
                cohortLoaneeRepository.findCohortLoaneeEntityByLoanee_IdAndCohort_Id(loaneeId,cohortId);
        log.info("After finding cohort loanee = : {}", cohortLoaneeProjection);

        return cohortLoaneeMapper.mapProjectionCohortLoanee(cohortLoaneeProjection);
    }

    @Override
    public CohortLoanee findCohortLoaneeByProgramIdAndLoaneeId(String programId, String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());

        CohortLoaneeEntity cohortLoaneeEntity =
                cohortLoaneeRepository.findCohortLoaneeEntityByCohort_ProgramIdAndLoanee_Id(programId,loaneeId);
        log.info("After finding cohort loanee by program ID  = : {}", cohortLoaneeEntity);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }

    @Override
    public List<CohortLoanee> findSelectedLoaneesInCohort(String id, List<String> loaneeIds) throws MeedlException {
        MeedlValidator.validateUUID(id, CohortMessages.INVALID_COHORT_ID.getMessage());

        List<CohortLoaneeEntity> cohortLoaneeEntities =
                cohortLoaneeRepository.findAllCohortLoaneeEntityBy_CohortIdAnd_ListOfLoaneeId(id,loaneeIds);

        log.info("found selected loanee = : {}", cohortLoaneeEntities);

        return cohortLoaneeEntities.stream().map(cohortLoaneeMapper::toCohortLoanee).collect(Collectors.toList());
    }

    @Override
    public boolean checkIfLoaneeHasBeenPreviouslyReferred(String loaneeId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Long count = cohortLoaneeRepository.countByLoaneeIdAndStatus(loaneeId);
        log.info("loan referral count = {}", count);
        return count > 1;
    }

    @Override
    public Page<CohortLoanee> findAllLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getCohortId(), CohortMessages.INVALID_COHORT_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdAt")));

        Page<CohortLoaneeEntity> cohortLoanees = cohortLoaneeRepository.
                findAllByCohortId(loanee.getCohortId(),loanee.getLoaneeStatus(),loanee.getUploadedStatus(),pageRequest);
        log.info("found all cohort loanee = : {}", cohortLoanees);

        return cohortLoanees.map(cohortLoaneeMapper::toCohortLoanee);
    }

    @Override
    public Page<CohortLoanee> searchForLoaneeInCohort(Loanee loanee, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(loanee.getCohortId(),CohortMessages.INVALID_COHORT_ID.getMessage());
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize,Sort.by(Sort.Order.desc("createdAt")));
        log.debug("Searching for loanees with params: cohortId={}, nameFragment={}, status={}, uploadedStatus={}",
                loanee.getCohortId(), loanee.getLoaneeName(), loanee.getLoaneeStatus(), loanee.getUploadedStatus());
        Page<CohortLoaneeEntity> cohortLoanees =
                cohortLoaneeRepository.findByCohortIdAndNameFragment(loanee.getCohortId(),
                        loanee.getLoaneeName(),loanee.getLoaneeStatus(), loanee.getUploadedStatus(),pageRequest);
        if (cohortLoanees.isEmpty()){
            return Page.empty();
        }
        return cohortLoanees.map(cohortLoaneeMapper::toCohortLoanee);
    }

    @Override
    public Page<CohortLoanee> findAllLoaneeThatBenefitedFromLoanProduct(String id, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loan product id cannot be empty");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<CohortLoaneeEntity> cohortLoanees =
                cohortLoaneeRepository.findAllLoanProductBenficiaryByLoanProductId(id,pageRequest);

        return cohortLoanees.map(cohortLoaneeMapper::toCohortLoanee);
    }

    @Override
    public Page<CohortLoanee> searchLoaneeThatBenefitedFromLoanProduct(String id, String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(id,"Loan product id cannot be empty");
        Pageable pageRequest = PageRequest.of(pageNumber, pageSize);


        Page<CohortLoaneeEntity> cohortLoaneeEntities =
                cohortLoaneeRepository.searchLoanBeneficiaryByLoanProductId(id,name,pageRequest);
        log.info("cohort loanee entities == {}",cohortLoaneeEntities);

        return cohortLoaneeEntities.map(cohortLoaneeMapper::toCohortLoanee);
    }

    @Override
    public void archiveOrUnArchiveByIds(String cohortId, List<String> loaneeIds, LoaneeStatus loaneeStatus) throws MeedlException {
        MeedlValidator.validateUUID(cohortId,CohortMessages.INVALID_COHORT_ID.getMessage());
        if (loaneeIds.isEmpty()){
            throw new LoanException(LoaneeMessages.LOANEES_ID_CANNOT_BE_EMPTY.getMessage());
        }
        cohortLoaneeRepository.updateStatusByIds(cohortId,loaneeIds, loaneeStatus);
    }

    @Override
    public CohortLoanee findCohortLoaneeByLoanRequestId(String loanRequestId) throws MeedlException {
        MeedlValidator.validateUUID(loanRequestId, "Loan request id is required");
        CohortLoaneeEntity cohortLoaneeEntity = cohortLoaneeRepository.findCohortLoaneeByLoanRequestId(loanRequestId);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }

    @Override
    public CohortLoanee findCohortLoaneeByLoaneeLoanDetailId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "Loanee loan detail id cannot be empty ");
        CohortLoaneeEntity cohortLoaneeEntity = cohortLoaneeRepository.findByLoaneeLoanDetailId(id);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }

    @Override
    public boolean checkIfLoaneeIsNew(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        Long count =  cohortLoaneeRepository.countByLoaneeId(id);
        return count > 0;
    }

    @Override
    public CohortLoanee findCohortLoaneeByLoanId(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, "Loan id cannot be empty ");
        CohortLoaneeEntity cohortLoaneeEntity =
                cohortLoaneeRepository.findCohortLoaneeByLoanId(id);
        return cohortLoaneeMapper.toCohortLoanee(cohortLoaneeEntity);
    }
}
