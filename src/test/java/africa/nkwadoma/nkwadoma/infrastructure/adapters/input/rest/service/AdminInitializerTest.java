package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.validation.MeedlValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

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
    private IdentityManagerOutputPort identityManagerOutPutPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private UserIdentity userIdentity;
    @BeforeEach
    void setUp() {
       userIdentity = getUserIdentity();

    }
    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email("kobih47727@paxnw.com")
                .firstName("test: super admin first name ")
                .lastName("test: super admin last name")
                .role(PORTFOLIO_MANAGER)
                .createdBy("61fb3beb-f200-4b16-ac58-c28d737b546c")
                .build();
    }
    @Test
    @Order(1)
    void initializeFirstUser() {
        UserIdentity invitedUserIdentity = null;
        try {
            invitedUserIdentity = adminInitializer.inviteFirstUser(userIdentity);

        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertNotNull(invitedUserIdentity);
        assertNotNull(invitedUserIdentity.getId());
    }
    @Test
    @Order(2)
    void findCreatedFirstUserOnKeycloak(){
        Optional<UserIdentity> foundUserIdentity = Optional.empty();
        try {
            foundUserIdentity = identityManagerOutPutPort.getUserByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.error("First user on keycloak not found in test {}", e.getMessage());
        }
        assertTrue(foundUserIdentity.isPresent());
    }

    @Test
    @Order(3)
    void findCreatedFirstUserOnDB(){
        UserIdentity foundUserIdentity = null;
        try {
            foundUserIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.error("First user on data base not found in test {}", e.getMessage());
        }
        assertNotNull(foundUserIdentity);
    }

    @Test
    @Order(4)
    void initializeAlreadyExistingUser(){
        UserIdentity existingUserIdentity = null;
        try {
            existingUserIdentity = adminInitializer.inviteFirstUser(userIdentity);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }finally {
            log.error("finally block initiated...");
            assertNotNull(existingUserIdentity);
            assertNotNull(existingUserIdentity.getId());
            try {
                identityManagerOutPutPort.deleteUser(existingUserIdentity);
                userIdentityOutputPort.deleteUserById(existingUserIdentity.getId());
            } catch (MeedlException ex) {
                log.error(ex.getMessage());
            }
            UserIdentity foundUserInDb = null;
            try {
                foundUserInDb = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
            } catch (MeedlException e) {
                log.error(e.getMessage());
            }
            assertNull(foundUserInDb);
        }
    }

}