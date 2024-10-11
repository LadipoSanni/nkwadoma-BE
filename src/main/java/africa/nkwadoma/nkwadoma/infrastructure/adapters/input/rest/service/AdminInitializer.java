package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.email.SendOrganizationEmployeeEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.input.identity.CreateOrganizationUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.UserEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {
    private final SendColleagueEmailUseCase sendEmail;
    private final UserIdentityOutputPort userIdentityOutputPort;
    private final IdentityManagerOutPutPort identityManagerOutPutPort;

    @Value("${superAdmin.email}")
    private String SUPER_ADMIN_EMAIL ;

    @Value("${superAdmin.firstName}")
    private String SUPER_ADMIN_FIRST_NAME ;

    @Value("${superAdmin.lastName}")
    private String SUPER_ADMIN_LAST_NAME ;
    @Value("${superAdmin.createdBy}")
    private String CREATED_BY;

    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email(SUPER_ADMIN_EMAIL)
                .firstName(SUPER_ADMIN_FIRST_NAME)
                .lastName(SUPER_ADMIN_LAST_NAME)
                .role(PORTFOLIO_MANAGER)
                .createdBy(CREATED_BY)
                .build();
    }

    public UserIdentity inviteFirstUser(UserIdentity userIdentity) throws MiddlException {
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        try {
            userIdentity = identityManagerOutPutPort.createUser(userIdentity);
            sendEmail.sendColleagueEmail(userIdentity);
        } catch (MiddlException e) {
            log.warn("Unable to create user on identity manager, error : {}", e.getMessage());
            UserRepresentation userRepresentation = identityManagerOutPutPort.getUserRepresentation(userIdentity, true);
            userIdentity.setId(userRepresentation.getId());
        }
        try {
            userIdentityOutputPort.save(userIdentity);
        } catch (MiddlException e) {
            log.warn("Unable to save user to user identity output port, error : {}", e.getMessage());
        }
        return userIdentity;
    }


    @PostConstruct
    public void init() throws MiddlException {
        inviteFirstUser(getUserIdentity());

    }
}
