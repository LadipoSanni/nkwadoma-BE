package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.email;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.NotificationFlag;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanRequest;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AsynchronousMailingAdapter implements AsynchronousMailingOutputPort {
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final LoaneeEmailUsecase loaneeEmailUsecase;
    private final UserEmailUseCase userEmailUseCase;
    private final FinancierEmailUseCase financierEmailUseCase;
    private final SendColleagueEmailUseCase sendEmail;
    private final OrganizationEmployeeEmailUseCase sendOrganizationEmployeeEmailUseCase;
    private final CohortLoaneeOutputPort cohortLoaneeOutputPort;
    private final MeedlNotificationOutputPort meedlNotificationOutputPort;

    @Async
    @Override
    public void notifyLoanReferralActors(List<LoanReferral> loanReferrals,List<Loanee> loanees, UserIdentity userIdentity){
        for (int loaneeCount = 0; loaneeCount < loanees.size(); loaneeCount++) {
            try {
                boolean previoslyReferred = cohortLoaneeOutputPort.checkIfLoaneeHasBeenPreviouslyReferred(
                        loanees.get(loaneeCount).getId());
                log.info("previosly referred: {}", previoslyReferred);
                if (previoslyReferred){
                    sendNotification(loanReferrals.get(loaneeCount).getId(),userIdentity, loanees.get(loaneeCount));
                }else {
                    refer(loanReferrals.get(loaneeCount).getId(),loanees.get(loaneeCount));
                }
                notifyAllPortfolioManagerForLoanReferral(loanReferrals);
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
        };
    }

    private void notifyAllPortfolioManagerForLoanReferral(List<LoanReferral> loanReferrals) throws MeedlException {
        String message = getEmailsOfUsersReferred(loanReferrals);
        for (UserIdentity userIdentity : userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {

            notifyPortfolioManagerForLoanReferral(userIdentity, message);
        }
    }
    public String getEmailsOfUsersReferred(List<LoanReferral> loanReferrals) {
        String initialMessage = """
                We are pleased to inform you that a new loanee has been referred for a loan under your management.\s
                
                Review the referral details and validate the applicant's information.\s
                Initiate the loan assessment process to evaluate the applicant's eligibility.\s
                Contact the applicant to gather any additional required documentation.""";
        String emails = loanReferrals.stream()
                .map(referral -> referral.getLoanee().getUserIdentity().getEmail())
                .collect(Collectors.joining("\n"));

        return initialMessage + "\n" + emails + "\n";
    }


    private void notifyPortfolioManagerForLoanReferral(UserIdentity userIdentity, String message) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(userIdentity)
                .title("A New Loan Referral Has Been Made")
                .notificationFlag(NotificationFlag.LOAN_REFERRAL)
                .contentDetail(message)
                .timestamp(LocalDateTime.now())
                .senderMail(userIdentity.getEmail())
                .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                .callToAction(true)
                .contentId(userIdentity.getId())
                .build();
        meedlNotificationOutputPort.save(meedlNotification);
    }

    private void sendNotification(String loanReferralId,UserIdentity userIdentity, Loanee loanee) throws MeedlException {
        MeedlNotification meedlNotification = MeedlNotification.builder()
                .user(loanee.getUserIdentity())
                .title("Loan Referral")
                .notificationFlag(NotificationFlag.LOAN_REFERRAL)
                .contentDetail("We’re excited to inform you that you’ve been referred for another loan.")
                .timestamp(LocalDateTime.now())
                .senderMail(userIdentity.getEmail())
                .senderFullName(userIdentity.getFirstName()+" "+ userIdentity.getLastName())
                .callToAction(true)
                .contentId(loanReferralId)
                .build();
        meedlNotificationOutputPort.save(meedlNotification);
    }

    private void notifyAllPortfolioManager() throws MeedlException {
        for (UserIdentity userIdentity : userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)) {
            notifyPortfolioManager(userIdentity);
        }
    }
    private void notifyPortfolioManager(UserIdentity userIdentity) throws MeedlException {
        loaneeEmailUsecase.sendLoaneeHasBeenReferEmail(userIdentity);

    }

    private void refer(String loanReferralId,Loanee loanee) throws MeedlException {
        loaneeEmailUsecase.referLoaneeEmail(loanReferralId,loanee);
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
                invite(loanee);
                notifyAllPortfolioManager();
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
        });
    }

    private void invite(Loanee loanee) throws MeedlException {
        loaneeEmailUsecase.inviteLoaneeEmail(loanee.getCohortLoaneeId(),loanee);

    }

    @Override
    public void sendLoanRequestDecisionMail(LoanRequest loanRequest) throws MeedlException {
        log.info("Sending loan request decision mail ...... {} ", loanRequest.getUserIdentity());
        loaneeEmailUsecase.sendLoanRequestApprovalEmail(loanRequest);
    }

    @Override
    public void notifyDeactivatedUser(UserIdentity userIdentity) {
        userEmailUseCase.sendDeactivatedUserEmailNotification(userIdentity);
    }

    @Override
    public void sendDeactivatedEmployeesEmailNotification(List<OrganizationEmployeeIdentity> organizationEmployees, OrganizationIdentity organization) throws MeedlException {
        organizationEmployees
                .forEach(this::notifyDeactivatedEmployee);

        notifyAllPortfolioManagerOnAccountDeactivation(organization);
    }

    private void notifyAllPortfolioManagerOnAccountDeactivation(OrganizationIdentity organization) throws MeedlException {
     notifyAllPortfolioManager();
    }

    private void notifyDeactivatedEmployee(OrganizationEmployeeIdentity employee) {
        userEmailUseCase.sendDeactivatedUserEmailNotification(employee.getMeedlUser());
    }

    private void emailInviteNonExistingFinancierToVehicle(Financier financier, InvestmentVehicle investmentVehicle) throws MeedlException {
        financierEmailUseCase.inviteFinancierToVehicle(financier.getUserIdentity(), investmentVehicle);
    }
    private void emailInviteNonExistingFinancierToPlatform(UserIdentity userIdentity) throws MeedlException {
        financierEmailUseCase.inviteFinancierToPlatform(userIdentity);
    }

}
