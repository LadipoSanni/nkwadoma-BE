package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.enums.constants.OrganizationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
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

import static africa.nkwadoma.nkwadoma.domain.enums.constants.notification.MeedlNotificationMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanRequestService implements LoanRequestUseCase {
    private final LoanRequestOutputPort loanRequestOutputPort;
    private final LoanProductOutputPort loanProductOutputPort;
    private final LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    private final LoanOfferUseCase loanOfferUseCase;
    private final LoaneeUseCase loaneeUseCase;
    private final LoanRequestMapper loanRequestMapper;
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final LoanMetricsOutputPort loanMetricsOutputPort;
    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final UserIdentityOutputPort userIdentityOutputPort;

    @Override
    public Page<LoanRequest> viewAllLoanRequests(LoanRequest loanRequest) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest, LoanMessages.LOAN_REQUEST_MUST_NOT_BE_EMPTY.getMessage());
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
    public LoanRequest viewLoanRequestById(LoanRequest loanRequest, String userId) throws MeedlException {
        MeedlValidator.validateObjectInstance(loanRequest, LoanMessages.LOAN_REQUEST_CANNOT_BE_EMPTY.getMessage());
        MeedlValidator.validateUUID(loanRequest.getId(), LoanMessages.LOAN_REQUEST_ID_CANNOT_BE_EMPTY.getMessage());
        loanRequest = loanRequestOutputPort.findById(loanRequest.getId());
        log.info("Found loan request: {}", loanRequest);
        List<LoaneeLoanBreakdown> loaneeLoanBreakdowns =
                loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByLoaneeId(loanRequest.getLoaneeId());
        log.info("Loanee loan breakdowns by loanee with ID: {}: {}", loanRequest.getLoaneeId(), loaneeLoanBreakdowns);
        Loanee loanee = new Loanee();
        try {
            loanee = loaneeUseCase.viewLoaneeDetails(loanRequest.getLoaneeId(), userId);
            log.info("Credit score returned: {}", loanee.getCreditScore());
        } catch (MeedlException e) {
            log.error("Error retrieving loanee credit score {}", e.getMessage());
        }
        loanRequest.setCreditScore(loanee.getCreditScore());
        loanRequest.setLoaneeLoanBreakdowns(loaneeLoanBreakdowns);
        log.info("Loan request details: {}", loanRequest);
        return loanRequest;
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
            if (!foundLoanRequest.getLoanee().getUserIdentity().isIdentityVerified() &&
            !foundLoanRequest.getLoanee().getOnboardingMode().equals(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS)){
                log.info("The loanee for this loan request is not verified. Onboarding mode is: {}. {}", foundLoanRequest.getLoanee().getOnboardingMode(), LoanMessages.LOAN_REQUEST_CANNOT_BE_APPROVED.getMessage());
                throw new LoanException(LoanMessages.LOAN_REQUEST_CANNOT_BE_APPROVED.getMessage());
            }
            updatedLoanRequest = approveLoanRequest(loanRequest, foundLoanRequest);
            updateLoanRequestOnMetrics(foundLoanRequest);
            LoanOffer loanOffer = loanOfferUseCase.createLoanOffer(updatedLoanRequest);
            updatedLoanRequest.setLoanOfferId(loanOffer.getId());
            updatedLoanRequest.setDateTimeOffered(loanOffer.getDateTimeOffered());
            log.info("Loan request updated: {}", updatedLoanRequest);
            sendNotification(loanRequest, loanOffer, updatedLoanRequest);
            return updatedLoanRequest;
        }
        else {
            log.info("Loan request is not accepted {}", loanRequest);
            updatedLoanRequest = declineLoanRequest(loanRequest, foundLoanRequest);
            updatedLoanRequest.setLoaneeId(foundLoanRequest.getLoanee().getId());

            sendLoanRequestDeclinedNotification(loanRequest, updatedLoanRequest);

            return loanRequestOutputPort.save(updatedLoanRequest);
        }
    }

    private void sendLoanRequestDeclinedNotification(LoanRequest loanRequest, LoanRequest updatedLoanRequest) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(loanRequest.getActorId());
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .contentId(updatedLoanRequest.getId())
                .contentDetail(MeedlNotificationMessages.LOAN_REQUEST_DECLINED_CONTENT.getMessage().concat(" "+loanRequest.getDeclineReason()))
                .title(LOAN_REQUEST.getMessage())
                .user(updatedLoanRequest.getLoanee().getUserIdentity())
                .senderFullName(userIdentity.getFirstName()+" "+userIdentity.getLastName())
                .senderMail(userIdentity.getEmail())
                .notificationFlag(NotificationFlag.LOAN_REQUEST)
                .build();
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }

    private void sendNotification(LoanRequest loanRequest, LoanOffer loanOffer, LoanRequest updatedLoanRequest) throws MeedlException {
        UserIdentity userIdentity = userIdentityOutputPort.findById(loanRequest.getActorId());
        MeedlNotification meedlNotification = buildUpLoanOfferNotification(loanOffer, updatedLoanRequest, userIdentity);
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }

    private static MeedlNotification buildUpLoanOfferNotification(LoanOffer loanOffer, LoanRequest updatedLoanRequest, UserIdentity userIdentity) {
        return MeedlNotification.builder()
                .contentId(loanOffer.getId())
                .title(LOAN_OFFER.getMessage())
                .user(updatedLoanRequest.getLoanee().getUserIdentity())
                .senderFullName(userIdentity.getFirstName()+" "+userIdentity.getLastName())
                .senderMail(userIdentity.getEmail())
                .notificationFlag(NotificationFlag.LOAN_OFFER)
                .contentDetail(LOAN_OFFER_CONTENT.getMessage()).build();
    }

    private void updateLoanRequestOnMetrics(LoanRequest loanRequest) throws MeedlException {
        Optional<OrganizationIdentity> organization =
                organizationIdentityOutputPort.findOrganizationByName(loanRequest.getReferredBy());
        if (organization.isEmpty()) {
            throw new EducationException(OrganizationMessages.ORGANIZATION_NOT_FOUND.getMessage());
        }
        Optional<LoanMetrics> loanMetrics =
                loanMetricsOutputPort.findByOrganizationId(organization.get().getId());
        if (loanMetrics.isEmpty()) {
            throw new LoanException("Organization has no loan metrics");
        }
        loanMetrics.get().setLoanRequestCount(
                loanMetrics.get().getLoanRequestCount() - 1
        );
        loanMetricsOutputPort.save(loanMetrics.get());
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
        loanProduct.setTotalNumberOfLoanee(
                loanProduct.getTotalNumberOfLoanee() + 1
        );
        loanProduct = loanProductOutputPort.save(loanProduct);
        foundLoanRequest.setStatus(LoanRequestStatus.APPROVED);
        foundLoanRequest.setLoaneeId(foundLoanRequest.getLoanee().getId());
        foundLoanRequest = loanRequestMapper.updateLoanRequest(loanRequest, foundLoanRequest);
        foundLoanRequest = loanRequestOutputPort.save(foundLoanRequest);
        foundLoanRequest.setLoanProduct(loanProduct);
        return foundLoanRequest;
    }
}
