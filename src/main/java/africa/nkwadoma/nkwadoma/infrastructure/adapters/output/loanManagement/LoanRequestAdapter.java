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
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanRequestAdapter implements LoanRequestOutputPort {
    private final LoanRequestRepository loanRequestRepository;
    private final LoanRequestMapper loanRequestMapper;

    @Override
    public LoanRequest save(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        loanRequest.validate();
        LoanRequestEntity loanRequestEntity = loanRequestMapper.toLoanRequestEntity(loanRequest);
        LoanRequestEntity savedLoanRequestEntity = loanRequestRepository.save(loanRequestEntity);
        return loanRequestMapper.toLoanRequest(savedLoanRequestEntity);
    }

    @Override
    public Optional<LoanRequest> findById(String loanRequestId) throws MeedlException {
        MeedlValidator.validateUUID(loanRequestId);
        Optional<LoanRequestProjection> loanRequestProjection =
                loanRequestRepository.findLoanRequestById(loanRequestId);
        if (loanRequestProjection.isEmpty()) {
            log.info("Empty Loan request returned");
            return Optional.empty();
        }
        LoanRequest loanRequest = loanRequestMapper.mapProjectionToLoanRequest(loanRequestProjection.get());
        log.info("Mapped Loan request: {}", loanRequest);
        return Optional.of(loanRequest);
    }

    @Override
    public void deleteLoanRequestById(String id) {
        loanRequestRepository.deleteById(id);
    }

    @Override
    public Page<LoanRequest> viewAll(int pageNumber, int pageSize) throws MeedlException {
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        Page<LoanRequestProjection> loanRequests = loanRequestRepository.findAllLoanRequests(
                PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("createdDate"))));
        log.info("Loan requests retrieved from DB: {}", loanRequests.getContent());
        return loanRequests.map(loanRequestMapper::mapProjectionToLoanRequest);
    }
}
