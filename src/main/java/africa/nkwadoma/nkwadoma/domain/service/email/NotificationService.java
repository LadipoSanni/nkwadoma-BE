package africa.nkwadoma.nkwadoma.domain.service.email;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages.*;
import static africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.UrlConstant.CREATE_PASSWORD_URL;

@RequiredArgsConstructor
@Slf4j
public class NotificationService implements SendOrganizationEmployeeEmailUseCase, SendColleagueEmailUseCase {
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
    public void sendColleagueEmail(UserIdentity userIdentity) throws MeedlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(COLLEAGUE_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();

        sendMail(userIdentity, email);
    }

    private String getLink(UserIdentity userIdentity) throws MeedlException {
        String token = tokenUtils.generateToken(userIdentity.getEmail());
        log.info(token);
        return baseUrl + CREATE_PASSWORD_URL + token;
    }

    private void sendMail(UserIdentity userIdentity, Email email) {
        try {
            emailOutputPort.sendEmail(email);
        } catch (MeedlException e) {
            log.error("Error sending email to {} user id is {}. The error received is : {}", userIdentity.getEmail(), userIdentity.getRole(), e.getMessage());
        }
    }

}
