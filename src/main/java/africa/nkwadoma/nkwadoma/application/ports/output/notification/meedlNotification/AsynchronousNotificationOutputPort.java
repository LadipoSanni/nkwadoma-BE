package africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AsynchronousNotificationOutputPort {
    void notifyPortfolioManagerOfNewFinancier(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle, UserIdentity actor);
    @Async
    void notifyPortfolioManagerOfNewOrganization(OrganizationIdentity organizationIdentity, NotificationFlag notificationFlag) throws MeedlException;
}
