package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagementOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class KeycloakAdapterTest {
    @Autowired
    private IdentityManagementOutputPort identityManagementOutputPort;
    private UserIdentity userIdentity;

    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("John");
        userIdentity.setLastName("Doe");
        userIdentity.setEmail("doe@example.com");
    }

    @Test
    void createUser() {
        UserIdentity createdUser = identityManagementOutputPort.createUser(userIdentity);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
    }
}