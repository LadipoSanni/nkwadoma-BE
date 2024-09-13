package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.IdentityVerificationResponse;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class PremblyAdapterTest {

@Autowired
private IdentityVerificationOutputPort identityVerificationOutPutPort;

    private IdentityVerification identityVerification;

    @BeforeEach
    void setUp(){
        identityVerification =   IdentityVerification.builder().
                number("12345678923").image("WWW.imageUrl.com").build();

    }

    @Test
    void verifyUserNin(){
       IdentityVerificationResponse response = identityVerificationOutPutPort.verifyIdentity(identityVerification);
        assertNotNull(response);

    }
    @Test
    void verifyUserInput(){
        identityVerification.setNumber("");
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }
}