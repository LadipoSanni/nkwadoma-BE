package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
public class LoanMetricsPersistenceAdapter implements LoanMetricsOutputPort {
    private final LoanMetricsRepository loanMetricsRepository;
    private final LoanMetricsMapper loanMetricsMapper;

    @Override
    public LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanMetrics, LoanMessages.LOAN_METRICS_ENTITY_MUST_NOT_BE_EMPTY.getMessage());
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
}
