package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.email.EmailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.email.Email;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.OrganizationIdentityValidator;
import africa.nkwadoma.nkwadoma.domain.validation.UserIdentityValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.IdentityMessages.CREATE_PASSWORD_URL;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MiddlMessages.EMAIL_INVITATION_SUBJECT;
import static africa.nkwadoma.nkwadoma.domain.enums.constants.MiddlMessages.ORGANIZATION_INVITATION_TEMPLATE;


@RequiredArgsConstructor
@Slf4j
public class OrganizationIdentityService implements CreateOrganizationUseCase, SendOrganizationEmployeeEmailUseCase {
    private final OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private final TokenGeneratorOutputPort tokenGeneratorOutputPort;
    private final EmailOutputPort emailOutputPort;
    @Value("${FRONTEND_URL}")
    private String baseUrl;


    @Override
    public OrganizationIdentity inviteOrganization(OrganizationIdentity organizationIdentity) throws MiddlException {
        OrganizationIdentityValidator.validateOrganizationIdentity(organizationIdentity);
        UserIdentityValidator.validateUserIdentity(organizationIdentity.getOrganizationEmployees());
        organizationIdentity = identityManagerOutPutPort.createOrganization(organizationIdentity);
        UserIdentity newUser = identityManagerOutPutPort.createUser(organizationIdentity.getOrganizationEmployees().get(0).getMiddlUser());
        OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        employeeIdentity.setMiddlUser(newUser);
        employeeIdentity.setOrganization(organizationIdentity.getId());

        //save entities to DB
        organizationIdentityOutputPort.save(organizationIdentity);
        OrganizationEmployeeIdentity organizationEmployeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
        organizationEmployeeIdentity.getMiddlUser().setCreatedAt(LocalDateTime.now().toString());
        userIdentityOutputPort.save(organizationEmployeeIdentity.getMiddlUser());
        organizationEmployeeIdentityOutputPort.save(organizationEmployeeIdentity);


        //send invite email to organization admin
        sendEmail(organizationEmployeeIdentity.getMiddlUser());

        log.info("sent email");
       return null;
    }


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
        log.info("sent email {}",email);
        emailOutputPort.sendEmail(email);

    }


    private String getLink(UserIdentity userIdentity) throws MiddlException {
        String token = tokenGeneratorOutputPort.generateToken(userIdentity.getEmail());
        log.info("{} ===>",token);
        return baseUrl + CREATE_PASSWORD_URL + token;
    }

}
