package africa.nkwadoma.nkwadoma.domain.service.notification;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.*;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlnotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailTokenOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.UserMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.investmentVehicle.FinancierMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.meedlexception.MeedlNotificationException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.notification.Email;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.thymeleaf.context.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;


@RequiredArgsConstructor
@Slf4j
public class NotificationService implements OrganizationEmployeeEmailUseCase, SendColleagueEmailUseCase ,
        LoaneeEmailUsecase, MeedlNotificationUsecase, FinancierEmailUseCase, UserEmailUseCase {
    private final EmailOutputPort emailOutputPort;
    private final AesOutputPort tokenUtils;
    private final EmailTokenOutputPort emailTokenManager;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final MeedlNotificationOutputPort meedlNotificationOutputPort;
    @Value("${FRONTEND_URL}")
    private String baseUrl;

    @Override
    public void sendEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity), userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(ORGANIZATION_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);

    }

    @Override
    public void sendForgotPasswordEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getForgotPasswordLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(RESET_PASSWORD.getMessage())
                .to(userIdentity.getEmail())
                .template(FORGOT_PASSWORD_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);

    }


    @Override
    public void sendColleagueEmail(String organizationName,UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndIndustryName(getLink(userIdentity),
                                                                               userIdentity.getFirstName(),
                                                                                organizationName);
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(COLLEAGUE_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);
    }

    private String getForgotPasswordLink(UserIdentity userIdentity) throws MeedlException {
        String token = emailTokenManager.generateToken(userIdentity.getEmail());
        log.info("Generated token {}", token);
        log.info("url {}", baseUrl + RESET_PASSWORD_URL+ token);
        return baseUrl + RESET_PASSWORD_URL+ token;
    }
    private String getLink(UserIdentity userIdentity) throws MeedlException {
        String token = emailTokenManager.generateToken(userIdentity.getEmail());
        log.info("Generated token {}", token);
        return baseUrl + CREATE_PASSWORD_URL + token;
    }

    private String getLoanOfferLink(String loanOfferId) {
        log.info("Loan offer ID: {}", loanOfferId);
        return baseUrl + UrlConstant.VIEW_LOAN_OFFER_URL + loanOfferId;
    }
    

    private String getLinkForLoanReferral(UserIdentity userIdentity, String loaneeReferralId) throws MeedlException {
        String token = emailTokenManager.generateToken(userIdentity.getEmail(),loaneeReferralId);
        return baseUrl + LOANEE_OVERVIEW + token ;
    }

    private void sendMail(UserIdentity userIdentity, Email email) {
        try {
            emailOutputPort.sendEmail(email);
        } catch (MeedlException e) {
            log.error("Error sending email to {} user id is {}. The error received is : {}", userIdentity.getEmail(), userIdentity.getRole(), e.getMessage());
        }
    }

    @Override
    public void referLoaneeEmail(String loanReferralId,Loanee loanee) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndIndustryNameAndLoanReferralId(getLink(loanee.getUserIdentity()),
                                                            loanReferralId,
                                                            loanee.getUserIdentity().getFirstName(),
                                                                loanee.getReferredBy());
        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOANEE_REFERRAL_SUBJECT.getMessage())
                .to(loanee.getUserIdentity().getEmail())
                .template(LoaneeMessages.LOANEE_REFERRAL.getMessage())
                .firstName(loanee.getUserIdentity().getFirstName())
                .build();
        sendMail(loanee.getUserIdentity(), email);
    }

    @Override
    public void inviteLoaneeEmail(String loanOfferId, Loanee loanee) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndIndustryNameAndLoanOfferId(getLink(loanee.getUserIdentity()),
                loanOfferId,
                loanee.getUserIdentity().getFirstName(),
                loanee.getReferredBy());
        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOANEE_REFERRAL_SUBJECT.getMessage())
                .to(loanee.getUserIdentity().getEmail())
                .template(LoaneeMessages.LOANEE_REFERRAL.getMessage())
                .firstName(loanee.getUserIdentity().getFirstName())
                .build();
        sendMail(loanee.getUserIdentity(), email);
    }


    @Override
    public void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOANEE_HAS_BEEN_REFERRED.getMessage())
                .to(userIdentity.getEmail())
                .template(LoaneeMessages.LOANEE_REFERRAL_INVITATION_SENT.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity,email);
    }

    @Override
    public void sendLoanRequestApprovalEmail(LoanRequest loanRequest) {
        Context context = emailOutputPort.getNameAndLinkContextAndLoanOfferId
                (loanRequest.getUserIdentity().getFirstName(),
                        getLoanOfferLink(loanRequest.getLoanOfferId()));

        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOAN_REQUEST_APPROVED.getMessage())
                .to(loanRequest.getUserIdentity().getEmail())
                .template(LoaneeMessages.LOAN_REQUEST_APPROVAL.getMessage())
                .firstName(loanRequest.getUserIdentity().getFirstName())
                .build();

        sendMail(loanRequest.getUserIdentity(), email);
    }

    @Override
    public void inviteLoaneeEmail(Loanee loanee) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndIndustryName(getLink(loanee.getUserIdentity()),
                loanee.getUserIdentity().getFirstName(),
                loanee.getReferredBy());
        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOANEE_REFERRAL_SUBJECT.getMessage())
                .to(loanee.getUserIdentity().getEmail())
                .template(LoaneeMessages.LOANEE_REFERRAL.getMessage())
                .firstName(loanee.getUserIdentity().getFirstName())
                .build();
        sendMail(loanee.getUserIdentity(), email);
    }

    @Override
    public void sendPortforlioManagerEmail(UserIdentity portfolioManager, LoanOffer loanOffer) {
        Context context = emailOutputPort.getNameAndLinkContextAndLoanOfferIdAndLoaneeId(portfolioManager.getFirstName(),
                getLoanOfferAndLoaneeLink(loanOffer.getId(),loanOffer.getLoaneeId()));

        if (loanOffer.getLoaneeResponse().equals(LoanDecision.ACCEPTED)){
            Email email =  Email.builder()
                    .context(context)
                    .subject(LoaneeMessages.LOAN_OFFER_ACCEPTED.getMessage())
                    .to(portfolioManager.getEmail())
                    .template(LoaneeMessages.LOAN_OFFER_ACCEPTED_TEMPLATE.getMessage())
                    .firstName(portfolioManager.getFirstName())
                    .build();
            sendMail(portfolioManager, email);
        }else {
            Email email =  Email.builder()
                    .context(context)
                    .subject(LoaneeMessages.LOAN_OFFER_DECLINED.getMessage())
                    .to(loanOffer.getUserIdentity().getEmail())
                    .template(LoaneeMessages.LOAN_OFFER_DECLINED_TEMPLATE.getMessage())
                    .firstName(portfolioManager.getFirstName())
                    .build();
            sendMail(loanOffer.getUserIdentity(), email);
        }


    }

    private String getLoanOfferAndLoaneeLink(String id, String loaneeId) {
        return getLoanOfferLink(id)+"&loaneeId="+loaneeId;
    }


    @Override
    public MeedlNotification sendNotification(MeedlNotification meedlNotification) throws MeedlException {
        meedlNotification.setTimestamp(LocalDateTime.now());
        meedlNotification.validate();
        UserIdentity userIdentity = userIdentityOutputPort.findById(meedlNotification.getUser().getId());
        if (ObjectUtils.isEmpty(userIdentity)) {
            throw new MeedlNotificationException("Un-Existing user cannot receive notification");
        }
        log.info("is read at the point of sending notification {}",meedlNotification.isRead());
        return meedlNotificationOutputPort.save(meedlNotification);
    }

    @Override
    public MeedlNotification viewNotification(String id, String notificationId) throws MeedlException {
        MeedlValidator.validateUUID(id,"User id cannot be empty");
        MeedlValidator.validateUUID(notificationId,"Notification id cannot be empty");
        UserIdentity userIdentity = userIdentityOutputPort.findById(id);
        MeedlNotification meedlNotification = meedlNotificationOutputPort.findNotificationById(notificationId);
        if (!meedlNotification.getUser().getId().equals(userIdentity.getId())) {
            throw new MeedlNotificationException("this notification is not assigned to this user");
        }
        meedlNotification.setRead(true);
        meedlNotificationOutputPort.save(meedlNotification);
        meedlNotification.setDuration(getDurationText(meedlNotification.getTimestamp()));
        return meedlNotification;
    }

    public String getDurationText(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(timestamp, now);
        long hours = ChronoUnit.HOURS.between(timestamp, now);
        long days = ChronoUnit.DAYS.between(timestamp, now);
        long months = ChronoUnit.MONTHS.between(timestamp, now);
        long years = ChronoUnit.YEARS.between(timestamp, now);
        if (minutes < 60) {
            return formatDuration(minutes, "minute");
        } else if (hours < 24) {
            return formatDuration(hours, "hour");
        } else if (days < 30) {
            return formatDuration(days, "day");
        } else if (days < 365) {
            return formatDuration(months, "month");
        } else {
            return formatDuration(years, "year");
        }
    }

    private String formatDuration(long value, String unit) {
        return value + " " + unit + (value != 1 ? "s" : "") + " ago";
    }

    @Override
    public Page<MeedlNotification> viewAllNotification(String id, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(id,"User id cannot empty");
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        UserIdentity userIdentity = userIdentityOutputPort.findById(id);
        return meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userIdentity.getId(),pageSize,pageNumber);
    }

    @Override
    public MeedlNotification fetchNotificationCount(String id) throws MeedlException {
        MeedlValidator.validateUUID(id,"User id cannot empty");
        UserIdentity userIdentity = userIdentityOutputPort.findById(id);
        return meedlNotificationOutputPort.getNotificationCounts(userIdentity.getId());
    }

    @Override
    public Page<MeedlNotification> searchNotification(String userId, String title, int pageSize, int pageNumber) throws MeedlException {
        MeedlValidator.validateUUID(userId, "User id cannot empty");
        MeedlValidator.validatePageNumber(pageNumber);
        MeedlValidator.validatePageSize(pageSize);
        UserIdentity userIdentity = userIdentityOutputPort.findById(userId);
        return meedlNotificationOutputPort.searchNotification(userIdentity.getId(), title, pageSize, pageNumber);
    }

    @Override
    public void inviteFinancierToPlatform(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(FinancierMessages.FINANCIER_INVITE_TO_VEHICLE.getMessage())
                .to(userIdentity.getEmail())
                .template(FinancierMessages.FINANCIER_INVITE_TO_VEHICLE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);
    }
    @Override
    public void inviteFinancierToVehicle(UserIdentity userIdentity, InvestmentVehicle investmentVehicle) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndInvestmentVehicleName(getLinkFinancierToVehicle(userIdentity, investmentVehicle),userIdentity.getFirstName(), investmentVehicle.getName());
        Email email = Email.builder()
                .context(context)
                .subject(FinancierMessages.FINANCIER_INVITE_TO_VEHICLE.getMessage())
                .to(userIdentity.getEmail())
                .template(FinancierMessages.FINANCIER_INVITE_TO_VEHICLE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);
    }
    private String getLinkFinancierToVehicle(UserIdentity userIdentity, InvestmentVehicle investmentVehicle) throws MeedlException {
        String token = emailTokenManager.generateToken(userIdentity.getEmail());
        log.info("Generated token for inviting financier to vehicle: {}", token);
        return baseUrl + CREATE_PASSWORD_URL + token + "?investmentVehicleId=" + investmentVehicle.getId();
    }

    @Override
    public void deleteMultipleNotification(String userId, List<String> notificationIdList) throws MeedlException {
        MeedlValidator.validateUUID(userId, MeedlMessages.USER_ID_CANNOT_BE_EMPTY.getMessage());
        notificationIdList = MeedlValidator.validateNotificationListAndFilter(notificationIdList);
        meedlNotificationOutputPort.deleteMultipleNotification(userId, notificationIdList);
    }

    @Override
    public void sendDeactivatedUserEmailNotification(UserIdentity userIdentity) {
        Context context = emailOutputPort.getNameAndDeactivationReasonContext(userIdentity.getFirstName(), userIdentity.getDeactivationReason());
        Email email = Email.builder()
                .context(context)
                .subject(UserMessages.USER_HAS_BEEN_DEACTIVATED.getMessage())
                .to(userIdentity.getEmail())
                .template(UserMessages.DEACTIVATED_USER.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);
    }
}
