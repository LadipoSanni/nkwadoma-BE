package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

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

    public UserIdentity inviteFirstUser(UserIdentity userIdentity) throws MeedlException {
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity = saveUserToKeycloak(userIdentity);
        UserIdentity foundUserIdentity = null;
        try {
            foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.warn("First user not found, creating first user: {}", e.getMessage());
        } finally {
            if (ObjectUtils.isEmpty(foundUserIdentity)) {
                saveUserToDB(userIdentity);
            }else {
                log.info("First user already exists");
            }
        }
        return userIdentity;
    }

    private void saveUserToDB(UserIdentity userIdentity) {
            try {
                userIdentityOutputPort.save(userIdentity);
                log.info("First user created successfully");
            } catch (MeedlException e) {
                log.error("Unable to save user to identity manager, error : {}", e.getMessage());
            }
    }

    private UserIdentity saveUserToKeycloak(UserIdentity userIdentity) throws MeedlException {
        try {
            userIdentity = identityManagerOutPutPort.createUser(userIdentity);
            sendEmail.sendColleagueEmail(userIdentity);
        } catch (MeedlException e) {
            log.warn("Unable to create user on identity manager, error : {}", e.getMessage());
            UserRepresentation userRepresentation = identityManagerOutPutPort.getUserRepresentation(userIdentity, Boolean.TRUE);
            userIdentity.setId(userRepresentation.getId());
        }
        return userIdentity;
    }


    @PostConstruct
    public void init() throws MeedlException {
        inviteFirstUser(getUserIdentity());

    }
}
