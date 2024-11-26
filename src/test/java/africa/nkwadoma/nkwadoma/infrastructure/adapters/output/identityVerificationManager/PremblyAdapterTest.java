package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.enums.prembly.PremblyResponseCode;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.InfrastructureException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.ImageConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class PremblyAdapterTest {

    @Mock
    private PremblyAdapter premblyAdapter;
    @Autowired
    @Qualifier("premblyAdapter")
    private IdentityVerificationOutputPort identityVerificationOutputPort;

    private IdentityVerification ninIdentityVerification;
    private IdentityVerification bvnIdentityVerification;
    private IdentityVerification livelinessVerification;

    private IdentityVerification identityVerification;
    private ImageConverter base64Converter;


    @BeforeEach
    void setUp() {
        bvnIdentityVerification = IdentityVerification.builder().bvn("1234567890")
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg").build();

        ninIdentityVerification = IdentityVerification.builder().nin("12345678903")
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg").build();

        livelinessVerification = IdentityVerification.builder()
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg").build();

        identityVerification =   IdentityVerification.builder().
                identityId("12345678901").identityImage("WWW.imageUrl.com").build();
    }

    @Test
    void verifyIdentityWithValidNinAndValidImage() throws IdentityManagerException, IdentityVerificationException {
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        log.info("Response {}",response);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getFaceData().getResponseCode());
        assertTrue(response.getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
        assertEquals(ninIdentityVerification.getIdentityNumber(), response.getNinData().getNin());
    }

    @Test
    void verifyIdentityWithNullIdentityVerification(){
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutputPort.verifyIdentity(null));
    }

    @Test
    void verifyIdentityWithNullIdentityId(){
        identityVerification.setIdentityId(null);
        assertThrows(InfrastructureException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityId(){
       identityVerification.setIdentityId(StringUtils.EMPTY);
       assertThrows(InfrastructureException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithNullIdentityImage(){
       identityVerification.setIdentityImage(null);
       assertThrows(InfrastructureException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityImage(){
       identityVerification.setIdentityImage(StringUtils.EMPTY);
       assertThrows(InfrastructureException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentity(){
        try {
            PremblyNinResponse response = identityVerificationOutputPort.verifyIdentity(identityVerification);
            log.info("Response ----> {}",response);
            assertNotNull(response);
        }catch (InfrastructureException e){
            log.info(e.getMessage());
        }
    }

}