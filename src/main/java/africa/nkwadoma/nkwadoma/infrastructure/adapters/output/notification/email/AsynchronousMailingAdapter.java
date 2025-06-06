package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.email;

import africa.nkwadoma.nkwadoma.application.ports.input.email.FinancierEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.LoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.OrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlNotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AsynchronousMailingAdapter implements AsynchronousMailingOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeEmailUsecase loaneeEmailUsecase;
    private final FinancierEmailUseCase financierEmailUseCase;
    private final SendColleagueEmailUseCase sendEmail;
    private final MeedlNotificationUsecase meedlNotificationUsecase;
    private final OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;

    @Async
    @Override
    public void notifyLoanReferralActors(List<Loanee> loanees){
        loanees.forEach(loanee -> {
            try {
                refer(loanee);
                notifyAllPortfolioManager();
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
        });
    }
    private void notifyAllPortfolioManager() throws MeedlException {
        for (UserIdentity userIdentity : userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            notifyPortfolioManager(userIdentity);
        }
    }
    private void notifyPortfolioManager(UserIdentity userIdentity) throws MeedlException {
        loaneeEmailUsecase.sendLoaneeHasBeenReferEmail(userIdentity);
    }

    private void refer(Loanee loanee) throws MeedlException {
        loaneeEmailUsecase.referLoaneeEmail(loanee);
    }
    @Async
    @Override
    public void sendFinancierEmail(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle) {
        if (ObjectUtils.isNotEmpty(investmentVehicle)){
            financiersToMail.forEach(financier -> {
                try {
                    emailInviteNonExistingFinancierToVehicle(financier, investmentVehicle);
                } catch (MeedlException e) {
                    throw new RuntimeException(e);
                }
            });

        }else {
            financiersToMail.forEach(financier -> {
                try {
                    emailInviteNonExistingFinancierToPlatform(financier.getUserIdentity());
                } catch (MeedlException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Async
    @Override
    public void sendColleagueEmail(String organizationName, UserIdentity userIdentity) throws MeedlException {
        sendEmail.sendColleagueEmail(organizationName,userIdentity);
    }

    @Async
    @Override
    public void sendEmailToInvitedOrganization(UserIdentity userIdentity) throws MeedlException {
        sendOrganizationEmployeeEmailUseCase.sendEmail(userIdentity);
    }
    @Async
    @Override
    public void sendLoaneeInvite(List<Loanee> loanees) {
        loanees.forEach(loanee -> {
            try {
                refer(loanee);
                notifyAllPortfolioManager();
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
        });
    }

    private void emailInviteNonExistingFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        financierEmailUseCase.inviteFinancierToVehicle(financier.getUserIdentity(), investmentVehicle);
    }
    private void emailInviteNonExistingFinancierToPlatform(UserIdentity userIdentity) throws MeedlException {
        financierEmailUseCase.inviteFinancierToPlatform(userIdentity);
    }

}
