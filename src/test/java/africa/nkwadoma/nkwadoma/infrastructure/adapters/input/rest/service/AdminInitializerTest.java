package africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.service;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
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
    @BeforeEach
    void setUp() {
    }
    @Test
    void initializeFirstUser(){
        try {
            OrganizationIdentity organizationIdentity = adminInitializer.inviteFirstUser(getOrganizationIdentity());
            assertNotNull(organizationIdentity);
            assertNotNull(organizationIdentity.getId());
        } catch (MiddlException e) {
            log.error("{}", e.getMessage());
//            assertTrue(false);
            throw new RuntimeException(e);
        }
    }
    private UserIdentity getUserIdentity() {
        return UserIdentity.builder()
                .email("initializertest@gmail.com")
                .firstName("test: super admin first name ")
                .lastName("test: super admin last name")
                .role(PORTFOLIO_MANAGER.name())
                .createdBy("ned")
                .build();
    }
    private OrganizationEmployeeIdentity getOrganizationEmployeeIdentity() {
        return OrganizationEmployeeIdentity.builder()
                .middlUser(getUserIdentity())
                .build();
    }
    private OrganizationIdentity getOrganizationIdentity() {

        return OrganizationIdentity.builder()
                .email("initializertest@gmail.com")
                .name("Middl")
                .phoneNumber("nil")
                .industry("Middl")
                .rcNumber("nil")
                .organizationEmployees(List.of(getOrganizationEmployeeIdentity()))
                .build();
    }
}