package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanRequestService implements LoanRequestUseCase {
    private final LoanRequestOutputPort loanRequestOutputPort;

    @Override
    public LoanRequest createLoanRequest(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        loanRequest.validate();
        MeedlValidator.validateObjectInstance(loanRequest.getLoanReferralStatus());
        if (!loanRequest.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_STATUS_MUST_BE_ACCEPTED.getMessage());
        }
        return loanRequestOutputPort.save(loanRequest);
    }

    @Override
    public Page<LoanRequest> viewAllLoanRequests(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        MeedlValidator.validatePageNumber(loanRequest.getPageNumber());
        MeedlValidator.validatePageSize(loanRequest.getPageSize());
        Page<LoanRequest> loanRequests = loanRequestOutputPort.viewAll(loanRequest.getPageNumber(), loanRequest.getPageSize());
        log.info("Loan requests from repository: {}", loanRequests.getContent());
        return loanRequests;
    }

    @Override
    public LoanRequest viewLoanRequestById(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        MeedlValidator.validateUUID(loanRequest.getId());
        Optional<LoanRequest> foundLoanRequest = loanRequestOutputPort.findById(loanRequest.getId());
        if (foundLoanRequest.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage());
        }
        return foundLoanRequest.get();
    }
}
