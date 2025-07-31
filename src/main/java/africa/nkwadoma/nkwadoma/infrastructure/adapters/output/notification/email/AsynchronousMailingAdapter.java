package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.notification.email;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.AsynchronousMailingOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
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
    private final AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;

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
                asynchronousNotificationOutputPort.notifyAllPortfolioManagerForLoanReferral(loanReferrals);
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
        }
    }

    @Async
    @Override
    public void sendLoaneeInvite(List<Loanee> loanees) {
        List<LoanReferral> loanReferrals = loanees.stream().map(loanee -> {
            try {
                invite(loanee);
            } catch (MeedlException e) {
                log.warn("Error sending actor email on loan referral {}", e.getMessage());
            }
            return LoanReferral.builder().loanee(loanee).build();
        }).toList();
        asynchronousNotificationOutputPort.notifyAllPortfolioManagerForLoanReferral(loanReferrals);
    }
    private void invite(Loanee loanee) throws MeedlException {
        loaneeEmailUsecase.inviteLoaneeEmail(loanee.getCohortLoaneeId(),loanee);
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
        asynchronousNotificationOutputPort.notifyAllPortfolioManagerForDeactivatedAccount();
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
