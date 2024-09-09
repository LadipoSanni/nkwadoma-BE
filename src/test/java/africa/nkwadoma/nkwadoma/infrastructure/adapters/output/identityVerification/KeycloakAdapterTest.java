package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerification;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutPutPort;
import africa.nkwadoma.nkwadoma.domain.model.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class KeycloakAdapterTest {
    @Autowired
    private IdentityManagerOutPutPort identityManagementOutputPort;
    private UserIdentity userIdentity;

    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("John");
        userIdentity.setLastName("Doe");
        userIdentity.setEmail("doe@example.com");
    }

    @Test
    void createUser() throws InfrastructureException {
        UserIdentity createdUser = identityManagementOutputPort.createUser(userIdentity);
        assertNotNull(createdUser);
        assertNotNull(createdUser.getUserId());
    }
}