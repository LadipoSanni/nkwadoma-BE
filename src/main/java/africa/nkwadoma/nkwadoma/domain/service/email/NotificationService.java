package africa.nkwadoma.nkwadoma.domain.service.email;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendLoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoaneeMessages;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanDecision;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.*;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.*;

@RequiredArgsConstructor
@Slf4j
public class NotificationService implements SendOrganizationEmployeeEmailUseCase, SendColleagueEmailUseCase , SendLoaneeEmailUsecase {
    private final EmailOutputPort emailOutputPort;
    private final TokenUtils tokenUtils;
    @Value("${FRONTEND_URL}")
    private String baseUrl;

    @Override
    public void sendEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity), userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(ORGANIZATION_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);

    }

    @Override
    public void sendForgotPasswordEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getForgotPasswordLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(RESET_PASSWORD.getMessage())
                .to(userIdentity.getEmail())
                .template(FORGOT_PASSWORD_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);

    }
    @Override
    public void sendColleagueEmail(String organizationName,UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndIndustryName(getLink(userIdentity),
                                                                               userIdentity.getFirstName(),
                                                                                organizationName);
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(COLLEAGUE_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity, email);
    }

    private String getForgotPasswordLink(UserIdentity userIdentity) throws MeedlException {
        String token = tokenUtils.generateToken(userIdentity.getEmail());
        log.info("Generated token {}", token);
        log.info("url {}", baseUrl + RESET_PASSWORD_URL+ token);
        return baseUrl + RESET_PASSWORD_URL+ token;
    }
    private String getLink(UserIdentity userIdentity) throws MeedlException {
        String token = tokenUtils.generateToken(userIdentity.getEmail());
        log.info("Generated token {}", token);
        return baseUrl + CREATE_PASSWORD_URL + token;
    }

    private String getLoanOfferLink(String loanOfferId) {
        log.info("Loan offer ID: {}", loanOfferId);
        return baseUrl + UrlConstant.VIEW_LOAN_OFFER_URL + loanOfferId;
    }

    private String getLoaneeLink(String loaneeId) {
        log.info("Loanee ID: {}", loaneeId);
        return "&loaneeId="+loaneeId;
    }

    private String getLinkForLoanReferral(UserIdentity userIdentity, String loaneeReferralId) throws MeedlException {
        String token = tokenUtils.generateToken(userIdentity.getEmail(),loaneeReferralId);
        return baseUrl + LOANEE_OVERVIEW + token ;
    }

    private void sendMail(UserIdentity userIdentity, Email email) {
        try {
            emailOutputPort.sendEmail(email);
        } catch (MeedlException e) {
            log.error("Error sending email to {} user id is {}. The error received is : {}", userIdentity.getEmail(), userIdentity.getRole(), e.getMessage());
        }
    }

    @Override
    public void referLoaneeEmail(Loanee loanee,String loaneeReferralId) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContextAndIndustryName(getLink(loanee.getUserIdentity()),
                                                            loanee.getUserIdentity().getFirstName(),
                                                                loanee.getReferredBy());
        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOANEE_REFERRAL_SUBJECT.getMessage())
                .to(loanee.getUserIdentity().getEmail())
                .template(LoaneeMessages.LOANEE_REFERRAL.getMessage())
                .firstName(loanee.getUserIdentity().getFirstName())
                .build();
        sendMail(loanee.getUserIdentity(), email);
    }



    @Override
    public void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOANEE_HAS_REFERRED.getMessage())
                .to(userIdentity.getEmail())
                .template(LoaneeMessages.LOANEE_REFERRAL_INVITATION_SENT.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity,email);
    }

    @Override
    public void sendLoanRequestApprovalEmail(LoanRequest loanRequest) {
        Context context = emailOutputPort.getNameAndLinkContextAndLoanOfferId
                (loanRequest.getLoanee().getUserIdentity().getFirstName(),
                        getLoanOfferLink(loanRequest.getLoanOfferId()));

        Email email = Email.builder()
                .context(context)
                .subject(LoaneeMessages.LOAN_REQUEST_APPROVED.getMessage())
                .to(loanRequest.getLoanee().getUserIdentity().getEmail())
                .template(LoaneeMessages.LOAN_REQUEST_APPROVAL.getMessage())
                .firstName(loanRequest.getLoanee().getUserIdentity().getFirstName())
                .build();

        sendMail(loanRequest.getUserIdentity(), email);
    }

    @Override
    public void sendPortforlioManagerEmail(UserIdentity portfolioManager, LoanOffer loanOffer) {
        Context context = emailOutputPort.getNameAndLinkContextAndLoanOfferIdAndLoaneeId
                (portfolioManager.getFirstName(),getLoanOfferLink(loanOffer.getId()),
                        getLoaneeLink(loanOffer.getLoaneeId()));

        if (loanOffer.getLoaneeResponse().equals(LoanDecision.ACCEPTED)){
            Email email =  Email.builder()
                    .context(context)
                    .subject(LoaneeMessages.LOAN_OFFER_ACCEPTED.getMessage())
                    .to(loanOffer.getUserIdentity().getEmail())
                    .template(LoaneeMessages.LOAN_OFFER_ACCEPTED_TEMPLATE.getMessage())
                    .firstName(portfolioManager.getFirstName())
                    .build();
            sendMail(loanOffer.getUserIdentity(), email);
        }else {
            Email email =  Email.builder()
                    .context(context)
                    .subject(LoaneeMessages.LOAN_OFFER_DECLINED.getMessage())
                    .to(loanOffer.getUserIdentity().getEmail())
                    .template(LoaneeMessages.LOAN_OFFER_DECLINED_TEMPLATE.getMessage())
                    .firstName(portfolioManager.getFirstName())
                    .build();
            sendMail(loanOffer.getUserIdentity(), email);
        }


    }




}
