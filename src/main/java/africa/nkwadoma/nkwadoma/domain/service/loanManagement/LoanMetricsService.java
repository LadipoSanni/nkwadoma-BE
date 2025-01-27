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
    private final ProgramOutputPort programOutputPort;
    private final LoanOfferUseCase loanOfferUseCase;
    private final LoanRequestOutputPort loanRequestOutputPort;
    private final LoanRequestUseCase loanRequestUseCase;
    private final LoanService loanService;
    private final ViewLoanProductUseCase viewLoanProductUseCase;

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
    public Page<LoanLifeCycle> searchLoan(String programId,String organizationId, LoanMetricsStatus status,String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectName(name,LoaneeMessages.LOANEE_NAME_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(programId,ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validateObjectInstance(status,"Status cannot be empty");
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);

        Program program = programOutputPort.findProgramById(programId);
        OrganizationIdentity organizationIdentity = programOutputPort.findCreatorOrganization(program.getCreatedBy());
        if(!organizationIdentity.getId().equals(organizationId)) {
            throw new LoanException("Program not in organization");
        }

        if (status.equals(LoanMetricsStatus.LOAN_OFFER)){
           Page<LoanOffer> loanOffers = loanOfferUseCase.searchForLoanOffer(programId,organizationId,name,pageSize,pageNumber);
        }
        if (status.equals(LoanMetricsStatus.LOAN_REQUEST)){
            Page<LoanRequest> loanRequests = loanRequestUseCase.searchForLoanRequest(programId,organizationId,name,pageSize,pageNumber);
        }
        if (status.equals(LoanMetricsStatus.LOAN_DISBURSAL)){
            Page<Loan> loans = viewLoanProductUseCase.searchForLoan(programId,organizationId,name,pageSize,pageNumber);
        }
        return null;
    }
}
