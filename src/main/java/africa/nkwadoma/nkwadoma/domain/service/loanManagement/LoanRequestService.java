package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanRequestService implements LoanRequestUseCase {
    private final LoanRequestOutputPort loanRequestOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    private final LoanOfferUseCase loanOfferUseCase;
    private final LoaneeUseCase loaneeUseCase;
    private final CreditRegistryOutputPort creditRegistryOutputPort;

    @Override
    public Page<LoanRequest> viewAllLoanRequests(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest);
        MeedlValidator.validatePageNumber(loanRequest.getPageNumber());
        MeedlValidator.validatePageSize(loanRequest.getPageSize());
        Page<LoanRequest> loanRequests;
        if (StringUtils.isNotEmpty(loanRequest.getOrganizationId())) {
            loanRequests = loanRequestOutputPort.viewAll
                    (loanRequest.getOrganizationId(), loanRequest.getPageNumber(), loanRequest.getPageSize());
        }
        else {
            loanRequests = loanRequestOutputPort.viewAll(loanRequest.getPageNumber(), loanRequest.getPageSize());
        }
        log.info("Loan requests from repository: {}", loanRequests.getContent());
        return loanRequests;
    }

    @Override
    public LoanRequest viewLoanRequestById(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest, LoanMessages.LOAN_REQUEST_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanRequest.getId(), LoanMessages.LOAN_REQUEST_ID_CANNOT_BE_EMPTY.getMessage());
        Optional<LoanRequest> foundLoanRequest = loanRequestOutputPort.findById(loanRequest.getId());
        if (foundLoanRequest.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage());
        }
        loanRequest = foundLoanRequest.get();
        log.info("Found loan request: {}", loanRequest);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllByLoaneeId(loanRequest.getLoaneeId());
        log.info("Loanee loan breakdowns by loanee with ID: {}: {}", loanRequest.getLoaneeId(), loaneeLoanBreakdowns);
        Loanee loanee = new Loanee();
        try {
            loanee = loaneeUseCase.viewLoaneeDetails(loanRequest.getLoaneeId());
        } catch (MeedlException e) {
            log.error("Error retrieving loanee credit score {}", e.getMessage());
        }
        log.info("Credit score returned: {}", loanee.getCreditScore());
        loanRequest.setCreditScore(loanee.getCreditScore());
        loanRequest.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
        return loanRequest;
    }

    @Override
    public LoanRequest respondToLoanRequest(LoanRequest loanRequest) throws MeedlException {
        LoanRequest.validate(loanRequest);
        LoanRequest foundLoanRequest = loanRequestOutputPort.findById(loanRequest.getId()).
                orElseThrow(()-> new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage()));
        log.info("Loan request retrieved: {}", foundLoanRequest);
        if (ObjectUtils.isNotEmpty(foundLoanRequest.getStatus())
                && foundLoanRequest.getStatus().equals(LoanRequestStatus.APPROVED)) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_HAS_ALREADY_BEEN_APPROVED.getMessage());
        }
        return respondToLoanRequest(loanRequest, foundLoanRequest);
    }

    private LoanRequest respondToLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        LoanRequest updatedLoanRequest = null;
        if (loanRequest.getLoanRequestDecision() == LoanDecision.ACCEPTED) {
            updatedLoanRequest = approveLoanRequest(loanRequest, foundLoanRequest);
        }
        else if (loanRequest.getLoanRequestDecision() == LoanDecision.DECLINED) {
            updatedLoanRequest = declineLoanRequest(loanRequest, foundLoanRequest);
        }
        return loanRequestOutputPort.save(updatedLoanRequest);
    }

    private static LoanRequest declineLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        MeedlValidator.validateDataElement(loanRequest.getDeclineReason(), "Reason for declining is required");
        foundLoanRequest.setLoanRequestDecision(loanRequest.getLoanRequestDecision());
        foundLoanRequest.setLoanAmountApproved(loanRequest.getLoanAmountApproved());
        foundLoanRequest.setStatus(LoanRequestStatus.DECLINED);
        return foundLoanRequest;
    }

    private LoanRequest approveLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(loanRequest.getLoanAmountApproved());
        if (loanRequest.getLoanAmountApproved().compareTo(foundLoanRequest.getLoanAmountRequested()) > 0) {
            throw new LoanException(LoanMessages.LOAN_AMOUNT_APPROVED_MUST_BE_LESS_THAN_OR_EQUAL_TO_REQUESTED_AMOUNT.getMessage());
        }
        LoanProduct loanProduct = loanProductOutputPort.findById(loanRequest.getLoanProductId());
        foundLoanRequest.setLoanProduct(loanProduct);
        foundLoanRequest.setStatus(LoanRequestStatus.APPROVED);
        foundLoanRequest.setLoanRequestDecision(loanRequest.getLoanRequestDecision());
        foundLoanRequest.setLoanAmountApproved(loanRequest.getLoanAmountApproved());
        LoanOffer loanOffer = loanOfferUseCase.createLoanOffer(foundLoanRequest);
        foundLoanRequest.setDateTimeOffered(loanOffer.getDateTimeOffered());
        return foundLoanRequest;
    }
}
