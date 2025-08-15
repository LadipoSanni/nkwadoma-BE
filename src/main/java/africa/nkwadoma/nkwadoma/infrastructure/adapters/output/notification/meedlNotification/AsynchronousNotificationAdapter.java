package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlnotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsynchronousNotificationAdapter implements AsynchronousNotificationOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final CohortOutputPort cohortOutputPort;
    private List<MeedlNotification> notificationsToSend;

    @Override
    @Async
    public void notifyPortfolioManagerOfNewFinancier(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle, UserIdentity actor) {
        notificationsToSend = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(investmentVehicle)){
            financiersToMail.forEach(financier -> {
               notificationsToSend.add(buildFinancierToInvestmentVehiclePmNotification(financier, investmentVehicle, actor));
            });
        }else {
            financiersToMail.forEach(financier -> {
               notificationsToSend.add(buildFinancierToPlatformPmNotification(financier, actor));
            });
        }
        for (MeedlNotification meedlNotification : notificationsToSend) {
            try {
                notifyPortfolioManagers(meedlNotification);
            } catch (MeedlException e) {
                log.error("FAILED NOTIFICATION: Notify financier notification to financiers wasn't sent due to {}",e.getMessage(), e);
            }
        }
    }

    @Async
    @Override
    public void notifySuperAdminOfNewOrganization(UserIdentity userIdentity,OrganizationIdentity organizationIdentity, NotificationFlag notificationFlag) throws MeedlException {
        List<UserIdentity> superAdmins = userIdentityOutputPort.findAllByRole(IdentityRole.MEEDL_SUPER_ADMIN);
        for (UserIdentity superAdmin : superAdmins) {
            MeedlNotification notification = MeedlNotification.builder()
                    .user(superAdmin)
                    .timestamp(LocalDateTime.now())
                    .contentId(organizationIdentity.getId())
                    .title("New Organization Invite Awaiting Approval")
                    .callToAction(Boolean.TRUE)
                    .senderMail(userIdentity.getEmail())
                    .senderFullName(userIdentity.getFirstName() + " " + userIdentity.getLastName())
                    .contentDetail("New organization with the name " + organizationIdentity.getName())
                    .notificationFlag(notificationFlag)
                    .build();
            meedlNotificationUsecase.sendNotification(notification);
        }
    }

    @Async
    @Override
    public void sendDeferralNotificationToEmployee(Loanee loanee, String loanId, NotificationFlag notificationFlag) throws MeedlException {
        Cohort cohort = cohortOutputPort.findCohortById(loanee.getCohortId());
        List<OrganizationEmployeeIdentity> organizationEmployeeIdentities = organizationEmployeeIdentityOutputPort
                .findAllByOrganization(cohort.getOrganizationId());
        for (OrganizationEmployeeIdentity organizationEmployeeIdentity : organizationEmployeeIdentities){
            MeedlNotification notification = MeedlNotification.builder()
                    .user(organizationEmployeeIdentity.getMeedlUser())
                    .timestamp(LocalDateTime.now())
                    .contentId(loanId)
                    .contentDetail(loanee.getUserIdentity().getFirstName() + " " + loanee.getUserIdentity().getLastName() + " requested to defer loan")
                    .senderMail(loanee.getUserIdentity().getEmail())
                    .senderFullName(loanee.getUserIdentity().getFirstName() + " " + loanee.getUserIdentity().getLastName())
                    .title("Defer Loan Request")
                    .notificationFlag(notificationFlag)
                    .build();
            meedlNotificationUsecase.sendNotification(notification);
        }
    }
    @Async
    @Override
    public void notifyPortfolioManagerOfNewLoanOfferWithDecision(LoanOffer loanOffer, UserIdentity userIdentity) throws MeedlException {
        MeedlNotification meedlNotification = buildLoanOfferPortfolioManagerNotification(loanOffer, userIdentity );
        notifyPortfolioManagers(meedlNotification);
        
    }
    @Async
    @Override
    public void notifyAllPortfolioManagerForLoanReferral(String message) throws MeedlException {
        for (UserIdentity userIdentity : userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            notifyPortfolioManagerForLoanReferral(userIdentity, message);
        }
    }
    private void notifyPortfolioManagerForLoanReferral(UserIdentity userIdentity, String message) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(userIdentity)
                .title("A New Loan Referral Has Been Made")
                .notificationFlag(NotificationFlag.LOAN_REFERRAL)
                .contentDetail(message)
                .timestamp(LocalDateTime.now())
                .senderMail(userIdentity.getEmail())
                .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                .callToAction(true)
                .contentId(userIdentity.getId())
                .build();
        meedlNotificationUsecase.sendNotification(meedlNotification);

    }


    @Override
    public void notifyAllPmForLoanRepaymentUploadFailure(StringBuilder validationErrorMessage) throws MeedlException {
        List<UserIdentity> portfolioManagers = userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);
        for (UserIdentity portfolioManager : portfolioManagers) {
            MeedlNotification notification = buildUploadFailureNotification(
                    portfolioManager,
                    "Failed to upload repayment history",
                    NotificationFlag.REPAYMENT_UPLOAD_FAILURE,
                    validationErrorMessage.toString()
            );

            meedlNotificationUsecase.sendNotification(notification);
        }
        log.info("Failure notification has been sent to all on possible malicious upload of repayment history. ");
    }
    @Override
    public void notifyAllPmForUserDataUploadFailure(StringBuilder validationErrorMessage) throws MeedlException {
        List<UserIdentity> portfolioManagers = userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER);
        for (UserIdentity portfolioManager : portfolioManagers) {
            MeedlNotification notification =  buildUploadFailureNotification(
                    portfolioManager,
                    "Failed to upload User data",
                    NotificationFlag.LOANEE_DATA_UPLOAD_FAILURE,
                    validationErrorMessage.toString()
            );
            meedlNotificationUsecase.sendNotification(notification);
        }
        log.info("Failure notification has been sent to all on possible malicious upload of user data. ");
    }
    private MeedlNotification buildUploadFailureNotification(
            UserIdentity portfolioManager,
            String title,
            NotificationFlag flag,
            String message
    ) {
        return MeedlNotification.builder()
                .user(portfolioManager)
                .timestamp(LocalDateTime.now())
                .contentId(portfolioManager.getId())
                .title(title)
                .callToAction(Boolean.TRUE)
                .senderMail(portfolioManager.getEmail())
                .senderFullName(portfolioManager.getFirstName() + " " + portfolioManager.getLastName())
                .contentDetail(message)
                .notificationFlag(flag)
                .build();
    }

    @Override
    public void notifyAllBackOfficeAdminForDeactivatedAccount(OrganizationIdentity organization) throws MeedlException {
        List<UserIdentity> backOfficeAdmins = userIdentityOutputPort.findAllByRoles(List.of(IdentityRole.MEEDL_SUPER_ADMIN, IdentityRole.MEEDL_ADMIN, IdentityRole.PORTFOLIO_MANAGER));
        for (UserIdentity backOfficeAdmin : backOfficeAdmins) {
            MeedlNotification notification = buildOrganizationStatusNotification(
                    backOfficeAdmin,
                    organization,
                    "deactivated",
                    NotificationFlag.ORGANIZATION_DEACTIVATED
            );
            meedlNotificationUsecase.sendNotification(notification);
        }
        log.info("Organization has been deactivated and all its admin. Notification sent.");
    }
    @Override
    public void notifyAllBackOfficeAdminForReactivatedAccount(OrganizationIdentity organization) throws MeedlException {
        List<UserIdentity> backOfficeAdmins = userIdentityOutputPort.findAllByRoles(List.of(IdentityRole.MEEDL_SUPER_ADMIN, IdentityRole.MEEDL_ADMIN, IdentityRole.PORTFOLIO_MANAGER));
        for (UserIdentity backOfficeAdmin : backOfficeAdmins) {
            MeedlNotification notification = buildOrganizationStatusNotification(
                    backOfficeAdmin,
                    organization,
                    "reactivated",
                    NotificationFlag.ORGANIZATION_REACTIVATED
            );
            meedlNotificationUsecase.sendNotification(notification);
        }
        log.info("Organization has been reactivated and all its admin. Notification sent.");
    }

    @Override
    public void notifySuperAdminOfActivationActivityAttempt(UserIdentity foundActor) throws MeedlException {
        List<UserIdentity> superAdmins = userIdentityOutputPort.findAllByRole(IdentityRole.MEEDL_SUPER_ADMIN);
        for (UserIdentity superAdmin : superAdmins) {
            MeedlNotification notification = MeedlNotification.builder()
                    .user(superAdmin)
                    .timestamp(LocalDateTime.now())
                    .contentId(foundActor.getId())
                    .title("Attempt to deactivate super admin made")
                    .callToAction(Boolean.TRUE)
                    .senderMail(foundActor.getEmail())
                    .senderFullName(superAdmin.getFirstName() + " " + superAdmin.getLastName())
                    .contentDetail("An attempt was made to deactivate the super admin account of Meedl's platform. \nThe attempt was made by "+foundActor.getFirstName() + " "+ foundActor.getLastName()+ ". User email is "+foundActor.getEmail() + ".\nUser role is "+foundActor.getRole())
                    .notificationFlag(NotificationFlag.MEEDL_SUPER_ADMIN_DEACTIVATION_ATTEMPT)
                    .build();
            meedlNotificationUsecase.sendNotification(notification);
        }
    }

    private MeedlNotification buildOrganizationStatusNotification(
            UserIdentity portfolioManager,
            OrganizationIdentity organization,
            String status,
            NotificationFlag flag
    ) {
        return MeedlNotification.builder()
                .user(portfolioManager)
                .timestamp(LocalDateTime.now())
                .contentId(organization.getId())
                .title("Organization has been " + status)
                .callToAction(Boolean.TRUE)
                .senderMail(portfolioManager.getEmail())
                .senderFullName(portfolioManager.getFirstName() + " " + portfolioManager.getLastName())
                .contentDetail("Organization with name " + organization.getName() + " has been " + status)
                .notificationFlag(flag)
                .build();
    }

    @Override
    public void notifyPmForLoanRepaymentUploadFailure(UserIdentity foundActor, StringBuilder validationErrorMessage, LoanBook loanBook) throws MeedlException {
        String contentId = getContentIdFromLoanBook(loanBook.getActorId(), loanBook);

        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(foundActor)
                .timestamp(LocalDateTime.now())
                .contentId(contentId)
                .title("Failed to upload repayment history: " + loanBook.getFile().getName())
                .callToAction(Boolean.FALSE)
                .senderMail(foundActor.getEmail())
                .senderFullName(foundActor.getFirstName() + " "+foundActor.getLastName())
                .contentDetail(validationErrorMessage.toString())
                .notificationFlag(NotificationFlag.REPAYMENT_UPLOAD_FAILURE)
                .build();
        notifyUploadFailureActors(List.of(IdentityRole.MEEDL_ADMIN), meedlNotification, foundActor);
        log.info("Failure notification sent to the actor with email : {}  and other meedl admins", foundActor.getEmail());

    }

    private void notifyUploadFailureActors(List<IdentityRole> identityRoles, MeedlNotification meedlNotification, UserIdentity foundActor) throws MeedlException {
        meedlNotificationUsecase.sendNotification(meedlNotification);
        List<UserIdentity> allActorsForFailureNotification = userIdentityOutputPort
                .findAllByRoles(identityRoles);
        for (UserIdentity userIdentity : allActorsForFailureNotification){
            meedlNotification.setUser(userIdentity);
            meedlNotification.setSenderFullName(userIdentity.getFirstName() + " "+userIdentity.getLastName());
            meedlNotification.setContentDetail("The user " + foundActor.getFirstName() + " " + foundActor.getFirstName() + " and email "+foundActor.getEmail()+ " encountered error on file upload. \n\n" +meedlNotification.getContentDetail());
            meedlNotification.setSenderMail(userIdentity.getEmail());
            meedlNotificationUsecase.sendNotification(meedlNotification);
        }

    }

    @Override
    public void notifyPmForUserDataUploadFailure(UserIdentity foundActor, StringBuilder validationErrorMessage, LoanBook loanBook) throws MeedlException {
        String contentId = getContentIdFromLoanBook(foundActor.getId(), loanBook);
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(foundActor)
                .timestamp(LocalDateTime.now())
                .contentId(contentId)
                .title("Failed to upload user data: " + loanBook.getFile().getName())
                .callToAction(Boolean.FALSE)
                .senderMail(foundActor.getEmail())
                .senderFullName(foundActor.getFirstName())
                .contentDetail(validationErrorMessage.toString())
                .notificationFlag(NotificationFlag.LOANEE_DATA_UPLOAD_FAILURE)
                .build();
        notifyUploadFailureActors(List.of(IdentityRole.MEEDL_ADMIN), meedlNotification, foundActor);
        log.info("Failure notification sent to the actor with email : {} ", foundActor.getEmail());
    }
    @Override
    public void notifyPmOnRepaymentUploadSuccess(UserIdentity foundActor, LoanBook loanBook) throws MeedlException {
        String contentId = getContentIdFromLoanBook(loanBook.getActorId(), loanBook);

        MeedlNotification meedlNotification = MeedlNotification.builder()
                .timestamp(LocalDateTime.now())
                .contentId(contentId)
                .title("Successfully Uploaded Repayment History")
                .callToAction(Boolean.TRUE)
                .senderMail(foundActor.getEmail())
                .senderFullName(foundActor.getFirstName())
                .contentDetail("Repayment history upload completed")
                .notificationFlag(NotificationFlag.REPAYMENT_UPLOAD_SUCCESS)
                .build();
        notifyPortfolioManagers(meedlNotification);
    }

    @Override
    public void notifyPmOnUserDataUploadSuccess(UserIdentity foundActor, LoanBook loanBook) throws MeedlException {
        String contentId = getContentIdFromLoanBook(loanBook.getActorId(), loanBook);

        MeedlNotification meedlNotification = MeedlNotification.builder()
                .timestamp(LocalDateTime.now())
                .contentId(contentId)
                .title("Successfully Uploaded User Data")
                .callToAction(Boolean.TRUE)
                .senderMail(foundActor.getEmail())
                .senderFullName(foundActor.getFirstName())
                .contentDetail("User data upload completed")
                .notificationFlag(NotificationFlag.LOANEE_DATA_UPLOAD_SUCCESS)
                .build();
        notifyPortfolioManagers(meedlNotification);
    }

    private static String getContentIdFromLoanBook(String actorId, LoanBook loanBook) {
        String contentId = actorId;
        if (ObjectUtils.isNotEmpty(loanBook.getCohort()) && ObjectUtils.isNotEmpty(loanBook.getCohort().getId())){
            contentId = loanBook.getCohort().getId();
        }
        return contentId;
    }

    private void notifyPortfolioManagers(MeedlNotification meedlNotification) throws MeedlException {
        for (UserIdentity userIdentity : userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            meedlNotification.setUser(userIdentity);
            log.info("Notifying portfolio manager on {} ", meedlNotification.getTitle());
        meedlNotificationUsecase.sendNotification(meedlNotification);
        }
    }

    private MeedlNotification buildFinancierToInvestmentVehiclePmNotification(Financier financier, InvestmentVehicle investmentVehicle, UserIdentity sender) {
        return MeedlNotification.builder()
                .title("Financier invited to platform")
                .contentId(financier.getId())
                .contentDetail("A new " + financier.getFinancierType().name().toLowerCase() +
                        " financier " + financier.getUserIdentity().getFirstName() +
                        " has been invited to the " + investmentVehicle.getName() +
                        " investment vehicle.\n" +
                        "Click the link to view financier detail.")
                .senderFullName(sender.getFirstName())
                .senderMail(sender.getEmail())
                .callToAction(true)
                .callToActionRoute("view/financier/details/not merge during this implementation. Should be updated")
                .notificationFlag(NotificationFlag.INVESTMENT_VEHICLE)
                .build();
    }

    private MeedlNotification buildFinancierToPlatformPmNotification(Financier financier, UserIdentity sender ) {
        return MeedlNotification.builder()
                .title("Financier invited to platform")
                .contentId(financier.getId())
                .contentDetail("A new " + financier.getFinancierType().name().toLowerCase() +
                        " financier " + financier.getUserIdentity().getFirstName() +
                        " has been invited to the platform." +
                        "Click the link to view financier detail.")
                .senderFullName(sender.getFirstName() +" "+ sender.getFirstName())
                .senderMail(sender.getEmail())
                .callToAction(true)
                .callToActionRoute("view/financier/details/not merge during this implementation. Should be updated")
                .notificationFlag(NotificationFlag.INVITE_FINANCIER)
                .build();
    }
    private MeedlNotification buildLoanOfferPortfolioManagerNotification(LoanOffer loanOffer, UserIdentity sender) {
        return MeedlNotification.builder()
                .title("Loan Offer Decision Made")
                .contentId(loanOffer.getId())
                .contentDetail("A Loan Offer has been "+ loanOffer.getLoaneeResponse())
                .senderFullName(sender.getFirstName() +" "+ sender.getFirstName())
                .senderMail(sender.getEmail())
                .callToAction(true)
                .callToActionRoute("view/loan/offer/not merge during this implementation. Should be updated")
                .notificationFlag(NotificationFlag.LOAN_OFFER_DECISION)
                .build();
    }

    @Override
   public void sendNotificationToSuperAdmin(OrganizationEmployeeIdentity foundActor, OrganizationEmployeeIdentity savedEmployee, OrganizationEmployeeIdentity organizationSuperAdmin) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .title("Pending colleague invitation")
                .contentDetail("Need Approval for colleague invitation")
                .senderFullName(foundActor.getMeedlUser().getFirstName()+" "+ foundActor.getMeedlUser().getLastName())
                .senderMail(foundActor.getMeedlUser().getEmail())
                .notificationFlag(NotificationFlag.INVITE_COLLEAGUE)
                .timestamp(LocalDateTime.now())
                .contentId(savedEmployee.getId())
                .callToAction(true)
                .user(organizationSuperAdmin.getMeedlUser())
                .build();
        log.info("done building notification for super admin to approve colleague invitation{}", meedlNotification);
        meedlNotificationUsecase.sendNotification(meedlNotification);
        log.info("notification sent ====---=-=---=-");
    }

    @Override
    public void sendDeclineColleagueNotification(OrganizationEmployeeIdentity organizationEmployeeIdentity,
                                                 UserIdentity userIdentity,UserIdentity createdBy) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .title("Decline colleague invitation")
                .contentDetail("Decline invitation for "+organizationEmployeeIdentity.getMeedlUser().getFirstName()+
                        " "+organizationEmployeeIdentity.getMeedlUser().getLastName())
                .senderFullName(userIdentity.getFirstName()+" "+userIdentity.getLastName())
                .senderMail(userIdentity.getEmail())
                .notificationFlag(NotificationFlag.DECLINE_COLLEAGUE_INVITE)
                .timestamp(LocalDateTime.now())
                .contentId(organizationEmployeeIdentity.getId())
                .callToAction(true)
                .user(createdBy)
                .build();
        log.info("done building notification for decline colleague invitation{}", meedlNotification);
        meedlNotificationUsecase.sendNotification(meedlNotification);
        log.info("notification sent ====---=-==---=-");
    }

    @Override
    public void sendNotificationToCooperateSuperAdmin(CooperateFinancier inviter, CooperateFinancier newCooperateFinancier, CooperateFinancier superAdminFinancier) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .title("Pending colleague invitation")
                .contentDetail("Need Approval for colleague invitation")
                .senderFullName(inviter.getFinancier().getUserIdentity().getFirstName()+" "+ inviter.getFinancier().getUserIdentity().getLastName())
                .senderMail(inviter.getFinancier().getUserIdentity().getEmail())
                .notificationFlag(NotificationFlag.INVITE_COLLEAGUE)
                .timestamp(LocalDateTime.now())
                .contentId(newCooperateFinancier.getId())
                .callToAction(true)
                .user(superAdminFinancier.getFinancier().getUserIdentity())
                .build();
        log.info("done building notification for super admin to approve colleague invitation{}", meedlNotification);
        meedlNotificationUsecase.sendNotification(meedlNotification);
        log.info("notification sent ====---=-=---=-");

    }
}
