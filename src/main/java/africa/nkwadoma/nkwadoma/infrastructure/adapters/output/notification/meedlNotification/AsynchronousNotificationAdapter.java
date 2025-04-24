package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsynchronousNotificationAdapter implements AsynchronousNotificationOutputPort {
    private MeedlNotificationUsecase meedlNotificationUsecase;
    private UserIdentityOutputPort userIdentityOutputPort;
    @Override
    @Async
    public void notifyPortfolioManagerOfNewFinancier(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle, UserIdentity actor) {
        if (ObjectUtils.isNotEmpty(investmentVehicle)){
            financiersToMail.forEach(financier -> {
                try {
                    notifyPortfolioManagerOfNonExistingFinancierToVehicle(financier, investmentVehicle, actor);
                } catch (MeedlException e) {
                    throw new RuntimeException(e);
                }
            });

        }else {
            financiersToMail.forEach(financier -> {
                try {
                    notifyPortfolioManagerOfNonExistingFinancierToPlatform(financier, actor);
                } catch (MeedlException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void notifyPortfolioManagerOfNonExistingFinancierToPlatform(Financier financier, UserIdentity actor) throws MeedlException {
        MeedlNotification meedlNotification = buildFinancierToPlatformPmNotification(financier, actor);
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }
    private void notifyPortfolioManagerOfNonExistingFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle, UserIdentity actor) throws MeedlException {
        MeedlNotification meedlNotification = buildFinancierToInvestmentVehiclePmNotification(financier,investmentVehicle, actor);
        meedlNotificationUsecase.sendNotification(meedlNotification);
    }

    private MeedlNotification buildFinancierToInvestmentVehiclePmNotification(Financier financier, InvestmentVehicle investmentVehicle, UserIdentity sender) {
        return MeedlNotification.builder()
                .title("Financier invited to platform")
                .user(financier.getUserIdentity())
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
                .build();
    }

    private MeedlNotification buildFinancierToPlatformPmNotification(Financier financier, UserIdentity sender ) {
        return MeedlNotification.builder()
                .title("Financier invited to platform")
                .user(financier.getUserIdentity())
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
                .build();
    }
}
