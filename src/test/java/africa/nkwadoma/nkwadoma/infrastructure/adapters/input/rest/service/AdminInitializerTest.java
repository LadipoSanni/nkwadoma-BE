package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlportfolio.PortfolioOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.meedlPortfolio.Portfolio;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.keycloak.representations.idm.ClientRepresentation;
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
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private PortfolioOutputPort portfolioOutputPort;
    private UserIdentity userIdentity;
    private OrganizationIdentity organizationIdentity;
    private Portfolio portfolio;
    private String portfolioId;
    @BeforeEach
    void setUp() {
       userIdentity = UserIdentity.builder()
               .email("kobih727@paxnw.com")
               .firstName("test: super admin first name ")
               .lastName("test: super admin last name")
               .role(PORTFOLIO_MANAGER)
               .createdBy("61fb3beb-f200-4b16-ac58-c28d737b546c")
               .build();

       portfolio = Portfolio.builder().portfolioName("Portfolio").build();
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
            foundUserIdentity = identityManagerOutputPort.getUserByEmail(userIdentity.getEmail());
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
    void findCreatedFirstOrganizationOnDB(){
        OrganizationIdentity foundOrganizationIdentity = null;
        try {
            foundOrganizationIdentity = organizationIdentityOutputPort.findByEmail("meedl@meedl.com");
        } catch (MeedlException e) {
            log.error("First organization on data base not found in test {}", e.getMessage());
        }
        assertNotNull(foundOrganizationIdentity);
        assertNotNull(foundOrganizationIdentity.getOrganizationEmployees());
        assertFalse(foundOrganizationIdentity.getOrganizationEmployees().isEmpty());
        assertNotNull(foundOrganizationIdentity.getOrganizationEmployees().get(0));
        assertNotNull(foundOrganizationIdentity.getOrganizationEmployees().get(0).getMeedlUser());
        assertEquals(foundOrganizationIdentity.getOrganizationEmployees().get(0).getOrganization(), foundOrganizationIdentity   .getId());
    }
    @Test
    @Order(5)
    void findCreatedFirstOrganizationOnKeycloak(){
        ClientRepresentation clientRepresentation = null;
        try {
            clientRepresentation = identityManagerOutputPort.getClientRepresentationByClientId("Meedl");
        } catch (MeedlException e) {
            log.error("Error getting client representation in test: {}", e.getMessage());
        }
        assertNotNull(clientRepresentation);
        assertNotNull(clientRepresentation.getName());
        log.info("Client representation {}", clientRepresentation.getName());
    }

    @Test
    @Order(6)
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
                identityManagerOutputPort.deleteUser(existingUserIdentity);
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

    @Test
    @Order(6)
    void createPortfolio() {
        Portfolio newPorfolio = null;
        try {
            newPorfolio = adminInitializer.createMeedlPortfolio(portfolio);
            portfolioId = newPorfolio.getId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNotNull(newPorfolio);
        assertEquals(portfolio.getPortfolioName(), newPorfolio.getPortfolioName());
    }

    @Test
    @Order(7)
    void portfolioAlreadyExistsDoesntCreateAnewOne() {
        Portfolio existingPorfolio = null;
        try {
            existingPorfolio = adminInitializer.createMeedlPortfolio(portfolio);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNotNull(existingPorfolio);
        assertEquals(portfolio.getPortfolioName(), existingPorfolio.getPortfolioName());
        assertEquals(portfolioId, existingPorfolio.getId());
    }


    @AfterAll
    void cleanUp() {
        portfolioOutputPort.delete(portfolioId);
    }



}