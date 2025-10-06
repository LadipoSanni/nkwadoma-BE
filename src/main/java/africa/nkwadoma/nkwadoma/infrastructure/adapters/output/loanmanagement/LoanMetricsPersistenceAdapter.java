package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanMetricsPersistenceAdapter implements LoanMetricsOutputPort {
    private final LoanMetricsRepository loanMetricsRepository;
    private final LoanMetricsMapper loanMetricsMapper;

    @Override
    public LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanMetrics, LoanMessages.LOAN_METRICS_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanMetrics.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        LoanMetricsEntity loanMetricsEntity = loanMetricsMapper.toLoanMetricsEntity(loanMetrics);
        return loanMetricsMapper.toLoanMetrics(loanMetricsRepository.save(loanMetricsEntity));
    }

    @Override
    public Optional<LoanMetrics> findTopOrganizationWithLoanRequest() {
        Optional<LoanMetricsEntity> loanMetricsEntity =
                loanMetricsRepository.findDistinctTopByOrderByLoanRequestCountDesc();
        if (loanMetricsEntity.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(loanMetricsMapper.toLoanMetrics(loanMetricsEntity.get()));
    }

    @Override
    public void delete(String loanMetricsId) throws MeedlException {
        MeedlValidator.validateUUID(loanMetricsId,
                OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        Optional<LoanMetricsEntity> loanMetricsEntity = loanMetricsRepository.findById(loanMetricsId);
        if (loanMetricsEntity.isPresent()) {
            loanMetricsRepository.delete(loanMetricsEntity.get());
        }
    }

    @Override
    public Optional<LoanMetrics> findByOrganizationId(String organizationId) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        Optional<LoanMetricsEntity> loanMetricsEntity = loanMetricsRepository.findByOrganizationId(organizationId);
        log.info("Loan metrics entity retrieved from db: {}", loanMetricsEntity);
        return loanMetricsEntity.map(loanMetricsMapper::toLoanMetrics);
    }

    @Override
    public LoanMetricsProjection calculateAllMetrics() {
        return loanMetricsRepository.calculateLoanMetrics();
    }
}
