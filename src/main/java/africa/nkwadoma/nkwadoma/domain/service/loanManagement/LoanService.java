package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoanService implements CreateLoanProductUseCase, ViewLoanProductUseCase, ViewLoanReferralsUseCase,
        RespondToLoanReferralUseCase, LoanOfferUseCase {
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoaneeOutputPort loaneeOutputPort;
    private final LoanProductMapper loanProductMapper;
    private final LoanRequestMapper loanRequestMapper;
    private final LoanRequestService loanRequestService;
    private final IdentityManagerOutputPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoanReferralOutputPort loanReferralOutputPort;
    private final LoanRequestOutputPort loanRequestOutputPort;
    private final LoanOfferOutputPort loanOfferOutputPort;
    private final LoanOutputPort loanOutputPort;
    private final LoanOfferMapper loanOfferMapper;


    @Override
    public LoanProduct createLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        loanProduct.validateLoanProductDetails();
        UserIdentity foundUser = userIdentityOutputPort.findById(loanProduct.getCreatedBy());
        identityManagerOutPutPort.verifyUserExistsAndIsEnabled(foundUser);
        log.info("Loan product {} created successfully", loanProduct.getName());
        return loanProductOutputPort.save(loanProduct);
    }
    @Override
    public void deleteLoanProductById(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateDataElement(loanProduct.getId());
        loanProductOutputPort.deleteById(loanProduct.getId());
    }
    @Override
    public Page<LoanProduct> viewAllLoanProduct(LoanProduct loanProduct) {
        return loanProductOutputPort.findAllLoanProduct(loanProduct);
    }

    @Override
    public LoanProduct updateLoanProduct(LoanProduct loanProduct) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanProduct);
        MeedlValidator.validateUUID(loanProduct.getId());
        LoanProduct foundLoanProduct = loanProductOutputPort.findById(loanProduct.getId());
        if (foundLoanProduct.getTotalNumberOfLoanees() > BigInteger.ZERO.intValue()){
            throw new LoanException("Loan product " + foundLoanProduct.getName() + " cannot be updated as it has already been loaned out");
        }
        foundLoanProduct = loanProductMapper.updateLoanProduct(foundLoanProduct,loanProduct);
        foundLoanProduct.setUpdatedAt(LocalDateTime.now());
        log.info("Loan product updated {}",  foundLoanProduct);

        return loanProductOutputPort.save(foundLoanProduct);
    }

    @Override
    public Loan startLoan(Loan loan) throws MeedlException {
        MeedlValidator.validateObjectInstance(loan);
        MeedlValidator.validateUUID(loan.getLoaneeId());
        Loanee foundLoanee = loaneeOutputPort.findByUserId(loan.getLoaneeId())
                            .orElseThrow(() -> new LoanException(LoanMessages.LOANEE_NOT_FOUND.getMessage()));
        loan.setLoanee(foundLoanee);
        loan.setLoanAccountId(getLoanAccountId(foundLoanee));
        loan.setStartDate(LocalDateTime.now());
        if (loan.getStartDate().isAfter(LocalDateTime.now())) {
            throw new MeedlException("Start date cannot be in the future.");
        }
        loan.setLoanStatus(LoanStatus.PERFORMING);
        loan = loanOutputPort.save(loan);
        return loan;
    }

    private String getLoanAccountId(Loanee foundLoanee) {
        return null;
    }

    @Override
    public LoanProduct viewLoanProductDetailsById(String loanProductId) throws MeedlException {
        MeedlValidator.validateUUID(loanProductId);
        return loanProductOutputPort.findById(loanProductId);
    }

    @Override
    public LoanReferral viewLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral);
        MeedlValidator.validateDataElement(loanReferral.getId());
        String loanReferralId = loanReferral.getId().trim();
        MeedlValidator.validateUUID(loanReferralId);
        Optional<LoanReferral> foundLoanReferral = loanReferralOutputPort.findLoanReferralById(loanReferralId);
        if (foundLoanReferral.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        } else {
            return foundLoanReferral.get();
        }
    }

    @Override
    public LoanReferral respondToLoanReferral(LoanReferral loanReferral) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanReferral);
        MeedlValidator.validateUUID(loanReferral.getId());
        loanReferral.validate();
        Optional<LoanReferral> foundLoanReferral = loanReferralOutputPort.findLoanReferralById(loanReferral.getId());
        if (foundLoanReferral.isEmpty()) {
            throw new LoanException(LoanMessages.LOAN_REFERRAL_NOT_FOUND.getMessage());
        }
        LoanReferral updatedLoanReferral = foundLoanReferral.get();
        if (updatedLoanReferral.getLoanReferralStatus().equals(LoanReferralStatus.ACCEPTED)) {
            LoanRequest loanRequest = loanRequestMapper.mapLoanReferralToLoanRequest(updatedLoanReferral);
            loanRequestService.createLoanRequest(loanRequest);
            updatedLoanReferral.setLoanReferralStatus(LoanReferralStatus.AUTHORIZED);
            updatedLoanReferral = loanReferralOutputPort.saveLoanReferral(updatedLoanReferral);
        }
        return updatedLoanReferral;
    }

    @Override
    public LoanRequest respondToLoanRequest(LoanRequest loanRequest) throws MeedlException {
        LoanRequest.validate(loanRequest);
        LoanRequest foundLoanRequest = loanRequestOutputPort.findById(loanRequest.getId());
        MeedlValidator.validateLoanRequest(foundLoanRequest);
        if (foundLoanRequest.getStatus().equals(LoanRequestStatus.APPROVED)) {
            throw new LoanException(LoanMessages.LOAN_REQUEST_HAS_ALREADY_BEEN_APPROVED.getMessage());
        }
        return respondToLoanRequest(loanRequest, foundLoanRequest);
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
        createLoanOffer(foundLoanRequest);
    }

    @Override
    public LoanOffer createLoanOffer(LoanRequest loanRequest) throws MeedlException {
        LoanOffer loanOffer = new LoanOffer();
        Optional<LoanRequest> loanRequest = loanRequestOutputPort.findById(loanRequestId);
        if (loanRequest.isEmpty()){
            throw new LoanException(LoanMessages.LOAN_REQUEST_NOT_FOUND.getMessage());
        }
        if (loanRequest.get().getStatus() != LoanRequestStatus.APPROVED){
            throw new LoanException(LoanMessages.LOAN_REQUEST_MUST_HAVE_BEEN_APPROVED.getMessage());
        }
        loanOffer.setLoanRequest(loanRequest.get());
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanProduct(loanRequest.getLoanProduct());
        loanOffer = loanOfferOutputPort.save(loanOffer);
        return loanOffer;
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
}
