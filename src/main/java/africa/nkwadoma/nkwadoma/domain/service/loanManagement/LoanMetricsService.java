package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanMetricsService implements LoanMetricsUseCase {
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final LoanMetricsMapper loanMetricsMapper;

    @Override
    public LoanMetrics saveOrUpdateLoanMetrics(LoanMetrics loanMetrics) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanMetrics, LoanMessages.LOAN_METRICS_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanMetrics.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        Optional<LoanMetrics> foundLoanMetrics = loanMetricsOutputPort.findByOrganizationId(loanMetrics.getOrganizationId());
        LoanMetrics metrics;
        if (foundLoanMetrics.isPresent()) {
            int loanRequestCount = foundLoanMetrics.get().getLoanRequestCount() + loanMetrics.getLoanRequestCount();
            int loanReferralCount = foundLoanMetrics.get().getLoanReferralCount() + loanMetrics.getLoanReferralCount();
            int loanOfferCount = foundLoanMetrics.get().getLoanOfferCount() + loanMetrics.getLoanOfferCount();
            int loanDisbursalCount = foundLoanMetrics.get().getLoanDisbursalCount() + loanMetrics.getLoanDisbursalCount();
            metrics = LoanMetrics.builder().
                    organizationId(foundLoanMetrics.get().getOrganizationId()).
                    loanReferralCount(loanReferralCount).
                    loanRequestCount(loanRequestCount).
                    loanOfferCount(loanOfferCount).
                    loanDisbursalCount(loanDisbursalCount).build();
        }
        else {
            metrics = LoanMetrics.builder().
                    organizationId(loanMetrics.getOrganizationId()).
                    loanReferralCount(loanMetrics.getLoanReferralCount()).
                    loanRequestCount(loanMetrics.getLoanRequestCount()).
                    loanOfferCount(loanMetrics.getLoanOfferCount()).
                    loanDisbursalCount(loanMetrics.getLoanDisbursalCount()).
                    build();
        }
        loanMetrics = loanMetricsOutputPort.save(metrics);
        log.info("Loan metrics saved: {}", loanMetrics);
        return loanMetrics;
    }
}
