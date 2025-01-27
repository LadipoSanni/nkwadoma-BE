package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.email.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.ProgramMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
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
    private final SendLoaneeEmailUsecase sendLoaneeEmailUsecase;
    private final CreditRegistryOutputPort creditRegistryOutputPort;
    private final LoanRequestMapper loanRequestMapper;

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
    public Page<LoanRequest> searchForLoanRequest(String programId, String organizationId, String name, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(organizationId, OrganizationMessages.INVALID_ORGANIZATION_ID.getMessage());
        MeedlValidator.validateObjectName(name, LoaneeMessages.LOANEE_NAME_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(programId, ProgramMessages.INVALID_PROGRAM_ID.getMessage());
        MeedlValidator.validatePageSize(pageSize);
        MeedlValidator.validatePageNumber(pageNumber);
        Pageable pageRequest = PageRequest.of(pageNumber,pageSize);
        return loanRequestOutputPort.searchLoanRequest(programId,organizationId,name,pageRequest);
    }

    @Override
    public LoanRequest respondToLoanRequest(LoanRequest loanRequest) throws MeedlException {
        LoanRequest.validate(loanRequest);
        LoanRequest foundLoanRequest = loanRequestOutputPort.findLoanRequestById(loanRequest.getId()).
                orElseThrow(()-> new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage()));
        log.info("Loan request retrieved: {}", foundLoanRequest);
        if (ObjectUtils.isNotEmpty(foundLoanRequest.getStatus())
                && foundLoanRequest.getStatus().equals(LoanRequestStatus.APPROVED)) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_HAS_ALREADY_BEEN_APPROVED.getMessage());
        }
        return respondToLoanRequest(loanRequest, foundLoanRequest);
    }

    private LoanRequest respondToLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        LoanRequest updatedLoanRequest;
        if (loanRequest.getLoanRequestDecision() == LoanDecision.ACCEPTED) {
            updatedLoanRequest = approveLoanRequest(loanRequest, foundLoanRequest);

            LoanOffer loanOffer = loanOfferUseCase.createLoanOffer(updatedLoanRequest);

            updatedLoanRequest.setLoanOfferId(loanOffer.getId());
            updatedLoanRequest.setDateTimeOffered(loanOffer.getDateTimeOffered());
            log.info("Loan request updated: {}", updatedLoanRequest);

            sendLoaneeEmailUsecase.sendLoanRequestApprovalEmail(updatedLoanRequest);
            return updatedLoanRequest;
        }
        else {
            updatedLoanRequest = declineLoanRequest(loanRequest, foundLoanRequest);
            return loanRequestOutputPort.save(updatedLoanRequest);
        }
    }

    private static LoanRequest declineLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        MeedlValidator.validateDataElement(loanRequest.getDeclineReason(), LoanMessages.REASON_FOR_DECLINING_IS_REQUIRED.getMessage());
        foundLoanRequest.setLoanRequestDecision(loanRequest.getLoanRequestDecision());
        foundLoanRequest.setLoanAmountApproved(loanRequest.getLoanAmountApproved());
        foundLoanRequest.setStatus(LoanRequestStatus.DECLINED);
        return foundLoanRequest;
    }

    private LoanRequest approveLoanRequest(LoanRequest loanRequest, LoanRequest foundLoanRequest) throws MeedlException {
        MeedlValidator.validateBigDecimalDataElement(loanRequest.getLoanAmountApproved(), LoanMessages.LOAN_AMOUNT_APPROVED_MUST_NOT_BE_EMPTY.getMessage());
        if (loanRequest.getLoanAmountApproved().compareTo(foundLoanRequest.getLoanAmountRequested()) > 0) {
            throw new LoanException(LoanMessages.LOAN_AMOUNT_APPROVED_MUST_BE_LESS_THAN_OR_EQUAL_TO_REQUESTED_AMOUNT.getMessage());
        }

        LoanProduct loanProduct = loanProductOutputPort.findById(loanRequest.getLoanProductId());
        foundLoanRequest.setStatus(LoanRequestStatus.APPROVED);
        foundLoanRequest.setLoaneeId(foundLoanRequest.getLoanee().getId());
        foundLoanRequest = loanRequestMapper.updateLoanRequest(loanRequest, foundLoanRequest);
        foundLoanRequest = loanRequestOutputPort.save(foundLoanRequest);
        foundLoanRequest.setLoanProduct(loanProduct);
        return foundLoanRequest;
    }
}
