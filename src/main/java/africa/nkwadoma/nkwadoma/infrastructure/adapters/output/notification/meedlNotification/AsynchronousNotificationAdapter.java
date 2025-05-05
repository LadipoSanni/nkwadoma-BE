package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsynchronousNotificationAdapter implements AsynchronousNotificationOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final MeedlNotificationUsecase meedlNotificationUsecase;
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

    private void notifyPortfolioManagers(MeedlNotification meedlNotification) throws MeedlException {
        notifyAllPortfolioManager(meedlNotification);
    }
    private void notifyAllPortfolioManager(MeedlNotification meedlNotification) throws MeedlException {
        for (UserIdentity userIdentity : userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            meedlNotification.setUser(userIdentity);
            meedlNotification.setNotificationFlag(NotificationFlag.FINANCIER);
            log.info("Notifying portfolio manager on financier ");
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
                .senderFullName(sender.getFirstName() +" "+ sender.getFirstName())
                .senderMail(sender.getEmail())
                .callToAction(true)
                .callToActionRoute("view/financier/details/not merge during this implementation. Should be updated")
                .notificationFlag(NotificationFlag.FINANCIER)
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
                .contentDetail("A new " + financier.getFinancierType().name().toLowerCase() + " financier has been invited to the platform. Click the link to view financier detail")
                .senderFullName(sender.getFirstName() +" "+ sender.getFirstName())
                .senderMail(sender.getEmail())
                .callToAction(true)
                .callToActionRoute("view/financier/details/not merge during this implementation. Should be updated")
                .notificationFlag(NotificationFlag.FINANCIER)
                .build();
    }
}
