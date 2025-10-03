package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loanManagement.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanMetricsService implements LoanMetricsUseCase {
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final LoanMetricsMapper loanMetricsMapper;
    private final LoanRequestOutputPort loanRequestOutputPort;

    @Override
    public LoanMetrics save(LoanMetrics loanMetrics) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanMetrics, LoanMessages.LOAN_METRICS_MUST_NOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanMetrics.getOrganizationId(), OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());

        OrganizationIdentity organizationIdentity = organizationIdentityOutputPort.findById(loanMetrics.getOrganizationId());
        log.info("Organization Identity: {}", organizationIdentity);

        Optional<LoanMetrics> foundLoanMetrics =
                loanMetricsOutputPort.findByOrganizationId(loanMetrics.getOrganizationId());

        LoanMetrics metrics = foundLoanMetrics
                .map(existingMetrics -> loanMetricsMapper.updateLoanMetrics(existingMetrics, loanMetrics))
                .orElse(loanMetrics);

        LoanMetrics savedMetrics = loanMetricsOutputPort.save(metrics);

        log.info("Loan metrics saved successfully: {}", savedMetrics);
        return savedMetrics;
    }

    @Override
    public LoanMetrics createLoanMetrics(String id) throws MeedlException {
        MeedlValidator.validateUUID(id, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        LoanMetrics loanMetrics = new LoanMetrics();
        log.info("Create loan metrics: {}", id);
        loanMetrics.setOrganizationId(id);
        return loanMetricsOutputPort.save(loanMetrics);
    }

    @Override
    public void correctLoanRequestCount() throws MeedlException {
        List<OrganizationIdentity> organizationIdentities =
                organizationIdentityOutputPort.findAllOrganization();
        log.info("Found {} organization identity", organizationIdentities.size());
        for (OrganizationIdentity organizationIdentity : organizationIdentities) {
            Optional<LoanMetrics> loanMetrics = loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId());
            log.info("Loan metrics found: {}", loanMetrics);
            if (loanMetrics.isPresent()) {
                loanMetrics.get().setLoanRequestCount(
                        loanRequestOutputPort.getCountOfAllVerifiedLoanRequestInOrganization(organizationIdentity.getId())
                );
                 log.info("Loan request count change: {}", loanMetrics.get().getLoanRequestCount());
                 loanMetricsOutputPort.save(loanMetrics.get());
                 log.info("Loan metrics saved successfully: {}", loanMetrics.get());
            }
        }
    }

}
