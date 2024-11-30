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
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoanOfferUseCase loanOfferUseCase;

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

    @Override
    public LoanRequest respondToLoanRequest(LoanRequest loanRequest) throws MeedlException {
        LoanRequest.validate(loanRequest);
        Optional<LoanRequest> foundLoanRequest = loanRequestOutputPort.findById(loanRequest.getId());
        if (foundLoanRequest.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage());
        }
        MeedlValidator.validateLoanRequest(foundLoanRequest.get());
        if (foundLoanRequest.get().getStatus().equals(LoanRequestStatus.APPROVED)) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_HAS_ALREADY_BEEN_APPROVED.getMessage());
        }
        return respondToLoanRequest(loanRequest, foundLoanRequest.get());
    }

    private LoanRequest respondToLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        if (loanRequest.getLoanRequestDecision() == LoanDecision.ACCEPTED) {
            approveLoanRequest(loanRequest, foundLoanRequest);
        }
        else if (loanRequest.getLoanRequestDecision() == LoanDecision.DECLINED) {
            declineLoanRequest(loanRequest, foundLoanRequest);
        }
        return loanRequestOutputPort.save(foundLoanRequest);
    }

    private static void declineLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        MeedlValidator.validateDataElement(loanRequest.getDeclineReason());
        foundLoanRequest.setLoanRequestDecision(loanRequest.getLoanRequestDecision());
        foundLoanRequest.setLoanAmountApproved(loanRequest.getLoanAmountApproved());
        foundLoanRequest.setStatus(LoanRequestStatus.DECLINED);
    }

    private void approveLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(loanRequest.getLoanAmountApproved());
        if (loanRequest.getLoanAmountApproved().compareTo(foundLoanRequest.getLoanAmountRequested()) > 0) {
            throw new LoanException(LoanMessages.LOAN_AMOUNT_APPROVED_MUST_BE_LESS_THAN_OR_EQUAL_TO_REQUESTED_AMOUNT.getMessage());
        }
        LoanProduct loanProduct = loanProductOutputPort.findById(loanRequest.getLoanProductId());
        foundLoanRequest.setLoanProduct(loanProduct);
        foundLoanRequest.setStatus(LoanRequestStatus.APPROVED);
        foundLoanRequest.setLoanRequestDecision(loanRequest.getLoanRequestDecision());
        foundLoanRequest.setLoanAmountApproved(loanRequest.getLoanAmountApproved());
        loanOfferUseCase.createLoanOffer(foundLoanRequest);
    }
}
