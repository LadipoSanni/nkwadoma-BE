package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.loanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.loan.RepaymentHIstoryException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.loanBook.RepaymentHistoryMapper;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryProjection;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.loanBook.RepaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RepaymentHistoryAdapter implements RepaymentHistoryOutputPort {
    private final RepaymentHistoryMapper repaymentHistoryMapper;
    private final RepaymentHistoryRepository repaymentHistoryRepository;

    @Override
    public RepaymentHistory save(RepaymentHistory repaymentHistory) throws MeedlException {
        MeedlValidator.validateObjectInstance(repaymentHistory,"RepaymentHistory cannot be empty");
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
        MeedlValidator.validateUUID(repaymentId,"RepaymentId cannot be empty");
        repaymentHistoryRepository.deleteById(repaymentId);
    }

    @Override
    public Page<RepaymentHistory> findRepaymentHistoryAttachedToALoaneeOrAll(RepaymentHistory repaymentHistory, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("paymentDateTime"));
        Page<RepaymentHistoryProjection> repaymentHistoryEntities =
                repaymentHistoryRepository.findRepaymentHistoryByLoaneeIdOrAll(repaymentHistory.getLoaneeId(),
                        repaymentHistory.getMonth(),repaymentHistory.getYear(),pageable);
        log.info("Repayment history entities content {}", repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory).getContent());
        return repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory);

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

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("paymentDateTime"));
        Page<RepaymentHistoryProjection> repaymentHistoryEntities =
                repaymentHistoryRepository.searchRepaymentHistory(repaymentHistory.getMonth(),repaymentHistory.getYear(),
                        repaymentHistory.getLoaneeName(),pageable);
        return repaymentHistoryEntities.map(repaymentHistoryMapper::mapProjecttionToRepaymentHistory);
    }
}
