package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    @Order(1)
    void initializeFirstUser(){
        UserIdentity userIdentity = null;
        try {
            userIdentity = adminInitializer.inviteFirstUser(getUserIdentity());
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }finally {
            log.error("finally block initiated...");
                assertNotNull(userIdentity);
                assertNotNull(userIdentity.getId());
        }
    }
    @Test
    @Order(2)
    void initializeAlreadyExistingUser(){
        UserIdentity userIdentity = null;
        try {
            userIdentity = adminInitializer.inviteFirstUser(getUserIdentity());
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }finally {
            log.error("finally block initiated...");
            try {
                assertNotNull(userIdentity);
                assertNotNull(userIdentity.getId());
                identityManagerOutPutPort.deleteUser(userIdentity);
                userIdentityOutputPort.deleteUserById(userIdentity.getId());
            } catch (MeedlException ex) {
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