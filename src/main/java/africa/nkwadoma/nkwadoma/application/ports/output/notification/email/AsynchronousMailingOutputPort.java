package africa.nkwadoma.nkwadoma.application.ports.output.notification.email;

import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AsynchronousMailingOutputPort {
    @Async
    void notifyLoanReferralActors(List<LoanReferral> loanReferrals , List<Loanee> loanees, UserIdentity userIdentity) throws MeedlException;

    @Async
    void sendFinancierEmail(List<Financier> financiersToMail, InvestmentVehicle investmentVehicle);

    @Async
    void sendColleagueEmail(String organizationName, UserIdentity userIdentity) throws MeedlException;

    @Async
    void sendEmailToInvitedOrganization(UserIdentity userIdentity) throws MeedlException;
    @Async
    void sendLoaneeInvite(List<Loanee> loanees);

    @Async
    void sendLoanRequestDecisionMail(LoanRequest loanRequest) throws MeedlException;

    @Async
    void notifyUserOnActivationActivityOnUserAccount(UserIdentity userIdentity, ActivationStatus activationStatus);

    @Async
    void sendDeactivatedEmployeesEmailNotification(List<OrganizationEmployeeIdentity> organizationEmployees, OrganizationIdentity foundOrganization) throws MeedlException;

    @Async
    void sendReactivatedEmployeesEmailNotification(List<OrganizationEmployeeIdentity> organizationEmployees, OrganizationIdentity foundOrganization) throws MeedlException;
}
