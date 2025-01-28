package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanMetricsStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.*;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoanMetricsService implements LoanMetricsUseCase {
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final LoanMetricsMapper loanMetricsMapper;

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

}
