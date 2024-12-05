package africa.nkwadoma.nkwadoma.domain.service.email;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendLoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;

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
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
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
        return baseUrl + RESET_PASSWORD_URL+ token;
    }
    private String getLink(UserIdentity userIdentity) throws MeedlException {
        String token = tokenUtils.generateToken(userIdentity.getEmail());
        log.info("Generated token {}", token);
        return baseUrl + CREATE_PASSWORD_URL + token;
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
                .subject(LOANEE_REFERRAL_SUBJECT.getMessage())
                .to(loanee.getUserIdentity().getEmail())
                .template(LOANEE_REFERRAL.getMessage())
                .firstName(loanee.getUserIdentity().getFirstName())
                .build();
        sendMail(loanee.getUserIdentity(), email);
    }



    @Override
    public void sendLoaneeHasBeenReferEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(LOANEE_HAS_REFERRED.getMessage())
                .to(userIdentity.getEmail())
                .template(LOANEE_REFERRAL_INVITATION_SENT.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        sendMail(userIdentity,email);
    }
}
