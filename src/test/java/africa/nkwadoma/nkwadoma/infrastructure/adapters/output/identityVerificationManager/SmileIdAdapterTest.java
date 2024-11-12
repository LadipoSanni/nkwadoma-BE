package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.ImageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class SmileIdAdapterTest {
    @Autowired
    @Qualifier("smileIdAdapter")
    private IdentityVerificationOutputPort identityVerificationOutPutPort;

    private IdentityVerification identityVerification;
    private ImageConverter base64Converter;

    @BeforeEach
    void setUp(){
        identityVerification =   IdentityVerification.builder().
                identityId("12345678901").identityImage("WWW.imageUrl.com").build();
    }

    @Test
    void verifyIdentityWithNullIdentityVerification(){
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutPutPort.verifyIdentity(null));
    }

    @Test
    void verifyIdentityWithNullIdentityId(){
        identityVerification.setIdentityId(null);
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityId(){
        identityVerification.setIdentityId(StringUtils.EMPTY);
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithNullIdentityImage(){
        identityVerification.setIdentityImage(null);
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityImage(){
        identityVerification.setIdentityImage(StringUtils.EMPTY);
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

//    @Test
//    void verifyIdentity(){
//        try {
//            PremblyNinResponse response = identityVerificationOutPutPort.verifyIdentity(identityVerification);
//            log.info("Response ----> {}",response);
//            assertNotNull(response);
//        }catch (InfrastructureException e){
//            log.info(e.getMessage());
//        }
//    }
}
