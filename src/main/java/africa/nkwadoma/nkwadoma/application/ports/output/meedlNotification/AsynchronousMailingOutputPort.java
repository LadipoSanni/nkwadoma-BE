package africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AsynchronousMailingOutputPort {
    @Async
    void notifyLoanReferralActors(List<Loanee> loanees);

    @Async
    void sendFinancierEmail(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle);
}
