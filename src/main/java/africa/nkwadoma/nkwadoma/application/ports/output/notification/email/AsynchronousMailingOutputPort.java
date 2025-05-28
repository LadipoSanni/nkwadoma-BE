package africa.nkwadoma.nkwadoma.application.ports.output.notification.email;

import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDateTime;
import java.util.List;

public interface AsynchronousMailingOutputPort {
    @Async
    void notifyLoanReferralActors(List<Loanee> loanees);

    @Async
    void sendFinancierEmail(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle);

    @Async
    void sendColleagueEmail(String organizationName, UserIdentity userIdentity) throws MeedlException;

    @Async
    void sendEmailToInvitedOrganization(UserIdentity userIdentity) throws MeedlException;

}
