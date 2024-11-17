package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;


import africa.nkwadoma.nkwadoma.application.ports.input.email.SendColleagueEmailUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
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
    private final IdentityManagerOutputPort identityManagerOutPutPort;

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
        log.info("First user, after saving on keycloak: {}", userIdentity);
        try {
            foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.warn("First user not found, creating first user: {}", e.getMessage());
        } finally {
            log.info("First user after finding, before saving to db: {}", foundUserIdentity);
            if (ObjectUtils.isEmpty(foundUserIdentity)) {
                saveUserToDB(userIdentity);
            }else {
                log.info("First user already exists");
            }
        }
        return userIdentity;
    }

    private void saveUserToDB(UserIdentity userIdentity) throws MeedlException {
            try {
                userIdentityOutputPort.save(userIdentity);
                log.info("First user created successfully");
            } catch (MeedlException e) {
                log.error("Unable to save user to identity manager, error : {}", e.getMessage());
                throw new MeedlException("Unable to save user to data base, error : " + e.getMessage());
            }
    }

    private UserIdentity saveUserToKeycloak(UserIdentity userIdentity) throws MeedlException {
        try {
            userIdentity = identityManagerOutPutPort.createUser(userIdentity);
            log.info("User created successfully on keycloak sending email to user");
            sendEmail.sendColleagueEmail(userIdentity);
        } catch (MeedlException e) {
            log.warn("Unable to create user on identity manager, error : {}", e.getMessage());
            UserRepresentation userRepresentation = identityManagerOutPutPort.getUserRepresentation(userIdentity, Boolean.TRUE);
            log.info("user representation email {} , id : {}", userRepresentation.getEmail(), userRepresentation.getId() );
            userIdentity.setId(userRepresentation.getId());
        }
        return userIdentity;
    }


    @PostConstruct
    public void init() throws MeedlException {
        inviteFirstUser(getUserIdentity());

    }
}
