package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.CohortMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.repayment.RepaymentMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.RepaymentHIstoryException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook.RepaymentHistoryMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepaymentHistoryAdapter implements RepaymentHistoryOutputPort {
    private final RepaymentHistoryMapper repaymentHistoryMapper;
    private final RepaymentHistoryRepository repaymentHistoryRepository;

    @Override
    public RepaymentHistory save(RepaymentHistory repaymentHistory) throws MeedlException {
        MeedlValidator.validateObjectInstance(repaymentHistory, RepaymentMessages.REPAYMENT_CANNOT_BE_NULL.getMessage());
        repaymentHistory.validate();
        log.info("Repayment history before mapping to entity in adapter {}", repaymentHistory);
        RepaymentHistoryEntity repaymentHistoryEntity = repaymentHistoryMapper.map(repaymentHistory);
        log.info("Repayment history after mapping to entity {}", repaymentHistory);
        RepaymentHistoryEntity savedRepaymentHistoryEntity = repaymentHistoryRepository.save(repaymentHistoryEntity);
        log.info("Repayment history after saving entity to db {}", repaymentHistory);
        return repaymentHistoryMapper.map(savedRepaymentHistoryEntity);
    }

    @Override

    public void delete(String repaymentId) throws MeedlException {
        MeedlValidator.validateUUID(repaymentId,RepaymentMessages.INVALID_REPAYMENT_ID_PROVIDED.getMessage());
        repaymentHistoryRepository.deleteById(repaymentId);
    }

    @Override
    public Page<RepaymentHistory> findRepaymentHistoryAttachedToALoaneeOrAll(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("paymentDateTime").descending());
        Page<RepaymentHistoryProjection> repaymentHistoryEntities =
                repaymentHistoryRepository.findRepaymentHistoryByLoaneeIdOrAll(repaymentHistory.getLoaneeId(),
                        repaymentHistory.getMonth(),repaymentHistory.getYear(),pageable);
        log.info("repayment history size retrieved from DB == {},-- repayment histories {}",
                repaymentHistoryEntities.getSize(),repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory).getContent().stream().toList());
        log.info("repayment history mapped  {}",repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory).getContent().stream().toList());
        return repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory);

    }

    @Override
    public RepaymentHistory getFirstAndLastYear(String loaneeId) {
        log.info("Fetching first and last year for loaneeId: {}", loaneeId);
        Map<String, Integer> years = repaymentHistoryRepository.getFirstAndLastYear(loaneeId);
        log.info("Retrieved years: firstYear = {}, lastYear = {}", years.get("firstYear"), years.get("lastYear"));
        return RepaymentHistory.builder().firstYear(years.get("firstYear")).lastYear(years.get("lastYear")).build();
    }

    @Override
    public RepaymentHistory findRepaymentHistoryById(String repaymentId) throws MeedlException {
        MeedlValidator.validateUUID(repaymentId,"Repayment History Id cannot be null");
        RepaymentHistoryEntity repaymentHistoryEntity = repaymentHistoryRepository.findById(repaymentId)
                .orElseThrow(()-> new RepaymentHIstoryException("Repayment History Not Found"));
        return repaymentHistoryMapper.map(repaymentHistoryEntity);
    }

    @Override
    public Page<RepaymentHistory> searchRepaymemtHistoryByLoaneeName(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("paymentDateTime").descending());
        Page<RepaymentHistoryProjection> repaymentHistoryEntities =
                repaymentHistoryRepository.searchRepaymentHistory(repaymentHistory.getMonth(),repaymentHistory.getYear(),
                        repaymentHistory.getLoaneeName(),pageable);
        return repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory);
    }
//    @Override
    public RepaymentHistory findLatestRepayment2(String loaneeId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());

        return repaymentHistoryRepository
                .findTopByLoaneeIdAndCohortIdOrderByPaymentDateTimeDesc(loaneeId, cohortId)
                .map(repaymentHistoryMapper::map)
                .orElse(null);
    }

    @Override
    public RepaymentHistory findLatestRepayment(String loaneeId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());

        return repaymentHistoryRepository
                .findTopByLoaneeIdAndCohortIdOrderByPaymentDateTimeDesc(loaneeId, cohortId)
                .map(repaymentHistoryMapper::map)
                .orElse(null);
    }

    @Override
    public List<RepaymentHistory> findAllRepaymentHistoryForLoan(String loaneeId, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(loaneeId, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());

        return repaymentHistoryRepository
                .findAllByLoanee_IdAndCohortIdOrderByPaymentDateTimeAsc(loaneeId, cohortId)
                .stream()
                .map(repaymentHistoryMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMultipleRepaymentHistory(List<String> repaymentHistoryIds) {
        List<RepaymentHistoryEntity> repaymentHistories = repaymentHistoryRepository.findAllById(
                repaymentHistoryIds.stream()
                        .filter(StringUtils::isNotEmpty)
                        .toList()
        );

        log.info("List of repayments to be deleted {}", repaymentHistories);

        repaymentHistoryRepository.deleteAll(repaymentHistories);
    }

    @Override
    public List<RepaymentHistory> saveAllRepaymentHistory(List<RepaymentHistory> currentRepaymentHistories) {
        List<RepaymentHistoryEntity> entitiesToSave = currentRepaymentHistories.stream()
                .map(repaymentHistoryMapper::map)
                .toList();

        List<RepaymentHistoryEntity> savedEntities = repaymentHistoryRepository.saveAll(entitiesToSave);

        return savedEntities.stream()
                .map(repaymentHistoryMapper::map)
                .toList();
    }

    @Override
    public boolean checkIfLoaneeHasMadeAnyRepayment(String id, String cohortId) throws MeedlException {
        MeedlValidator.validateUUID(id, LoaneeMessages.INVALID_LOANEE_ID.getMessage());
        MeedlValidator.validateUUID(cohortId, CohortMessages.INVALID_COHORT_ID.getMessage());
        return repaymentHistoryRepository.checkIfLoaneeHaveAnyRepayment(id,cohortId);
    }

}
