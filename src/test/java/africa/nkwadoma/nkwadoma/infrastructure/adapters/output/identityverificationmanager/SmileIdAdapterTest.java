package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityverificationmanager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.IdentityException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.ImageConverter;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
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
    void setUp() {
        identityVerification = TestData.createTestIdentityVerification("12345678993","23456781234");
    }

    @Test
    void verifyIdentityWithNullIdentityVerification(){
        assertThrows(IdentityException.class, ()-> identityVerificationOutPutPort.verifyIdentity(null));
    }

    @Test
    void verifyIdentityWithNullIdentityId(){
        identityVerification.setIdentityId(null);
        assertThrows(IdentityException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityId(){
        identityVerification.setIdentityId(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithNullIdentityImage(){
        identityVerification.setImageUrl(null);
        assertThrows(IdentityException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityImage(){
        identityVerification.setImageUrl(StringUtils.EMPTY);
        assertThrows(IdentityException.class, ()-> identityVerificationOutPutPort.verifyIdentity(identityVerification));
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
