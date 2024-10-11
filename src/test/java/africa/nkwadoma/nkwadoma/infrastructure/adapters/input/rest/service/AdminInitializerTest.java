package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager.KeycloakAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class AdminInitializerTest {
    @Autowired
    private AdminInitializer adminInitializer;
    @Autowired
    private IdentityManagerOutPutPort identityManagerOutPutPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @BeforeEach
    void setUp() {
    }
    @Test
    void initializeFirstUser(){
        UserIdentity userIdentity = null;
        try {
            userIdentity = adminInitializer.inviteFirstUser(getUserIdentity());
        } catch (MiddlException e) {
            log.error("{}", e.getMessage());
        }finally {
            log.error("finally block initiated...");
            try {
                assertNotNull(userIdentity);
                assertNotNull(userIdentity.getId());
                identityManagerOutPutPort.deleteUser(userIdentity);
                userIdentityOutputPort.deleteUserById(userIdentity.getId());
            } catch (MiddlException ex) {
                log.error(ex.getMessage());
            }
        }
    }
    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email("kobih47727@paxnw.com")
                .firstName("test: super admin first name ")
                .lastName("test: super admin last name")
                .role(PORTFOLIO_MANAGER)
                .createdBy("ned")
                .build();
    }

}