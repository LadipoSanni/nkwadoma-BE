package africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification;

import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanOffer;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanBook;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AsynchronousNotificationOutputPort {
    @Async
    void notifyPortfolioManagerOfNewFinancier(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle, UserIdentity actor);
    @Async
    void notifyPortfolioManagerOfNewOrganization(OrganizationIdentity organizationIdentity, NotificationFlag notificationFlag) throws MeedlException;

    @Async
    void sendDeferralNotificationToEmployee(Loanee loanee, String loanId, NotificationFlag notificationFlag) throws MeedlException;
    @Async
    void notifyPortfolioManagerOfNewLoanOfferWithDecision(LoanOffer loanOffer, UserIdentity userIdentity) throws MeedlException;

    @Async
    void notifyPmForLoanRepaymentUploadFailure(UserIdentity foundActor, StringBuilder validationErrorMessage, String fileName) throws MeedlException;

    void notifyPmForUserDataUploadFailure(UserIdentity foundActor, StringBuilder validationErrorMessage, LoanBook name) throws MeedlException;

    void notifyPmOnRepaymentUploadSuccess(UserIdentity foundActor, LoanBook loanBook) throws MeedlException;

    void notifyPmOnUserDataUploadSuccess(UserIdentity foundActor, LoanBook loanBook) throws MeedlException;
}
