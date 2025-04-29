package africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;

import java.util.List;

public interface AsynchronousNotificationOutputPort {
    void notifyPortfolioManagerOfNewFinancier(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle, UserIdentity actor);
}
