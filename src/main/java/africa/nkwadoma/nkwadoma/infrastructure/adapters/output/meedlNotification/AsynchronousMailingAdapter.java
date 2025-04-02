package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.meedlNotification;

import africa.nkwadoma.nkwadoma.application.ports.input.email.FinancierEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.LoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AsynchronousMailingAdapter implements AsynchronousMailingOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeEmailUsecase loaneeEmailUsecase;
    private final FinancierEmailUseCase financierEmailUseCase;

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
    private void emailInviteNonExistingFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        financierEmailUseCase.inviteFinancierToVehicle(financier.getUserIdentity(), investmentVehicle);
    }
    private void emailInviteNonExistingFinancierToPlatform(UserIdentity userIdentity) throws MeedlException {
        financierEmailUseCase.inviteFinancierToPlatform(userIdentity);
    }

}
