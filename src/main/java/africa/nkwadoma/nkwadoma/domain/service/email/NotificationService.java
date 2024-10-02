package africa.nkwadoma.nkwadoma.domain.service.email;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.CREATE_PASSWORD_URL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MiddlMessages.*;

@RequiredArgsConstructor
public class NotificationService implements SendOrganizationEmployeeEmailUseCase, SendColleagueEmailUseCase {
    private final EmailOutputPort emailOutputPort;
    private final TokenGeneratorOutputPort tokenGeneratorOutputPort;
    @Value("${FRONTEND_URL}")
    private String baseUrl;

    @Override
    public void sendEmail(UserIdentity userIdentity) throws MiddlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(ORGANIZATION_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        emailOutputPort.sendEmail(email);

    }


    @Override
    public void sendColleagueEmail(UserIdentity userIdentity) throws MiddlException {
        Context context = emailOutputPort.getNameAndLinkContext(getLink(userIdentity),userIdentity.getFirstName());
        Email email = Email.builder()
                .context(context)
                .subject(EMAIL_INVITATION_SUBJECT.getMessage())
                .to(userIdentity.getEmail())
                .template(COLLEAGUE_INVITATION_TEMPLATE.getMessage())
                .firstName(userIdentity.getFirstName())
                .build();
        emailOutputPort.sendEmail(email);
    }

    private String getLink(UserIdentity userIdentity) throws MiddlException {
        String token = tokenGeneratorOutputPort.generateToken(userIdentity.getEmail());
        return baseUrl + CREATE_PASSWORD_URL + token;
    }
}
