package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityVerificationManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityVerificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.IdentityVerification;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyBvnResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyLivelinessResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyNinResponse;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.data.response.premblyresponses.PremblyResponse;
import africa.nkwadoma.nkwadoma.infrastructure.enums.prembly.PremblyResponseCode;
import africa.nkwadoma.nkwadoma.infrastructure.enums.prembly.PremblyVerificationMessage;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.IdentityVerificationException;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.ImageConverter;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;


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
        bvnIdentityVerification = TestData.createTestIdentityVerification("12345678903", "12345678903");
        ninIdentityVerification = TestData.createTestIdentityVerification("12345678903", "12345678903");

        livelinessVerification = IdentityVerification.builder()
                .imageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027769/.jpg").build();

        identityVerification =  IdentityVerification.builder().
                identityId("12345678901").imageUrl("WWW.imageUrl.com").build();
    }

    @Test
    void verifyIdentityWithValidNinAndValidImage() throws MeedlException {
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        log.info("Response {}",response);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getFaceData().getResponseCode());
        assertTrue(response.getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
        assertEquals(ninIdentityVerification.getNin(), response.getNinData().getNin());
    }
    @Test
    void verifyIdentityWithValidAndImageDoesNotMatch() throws MeedlException {
        ninIdentityVerification.setImageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732042468/gi2ppo8hsivajcn74idz.jpg");
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL_RECORD_NOT_FOUND.getCode(), response.getFaceData().getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyVerificationMessage.VERIFIED.getMessage(), response.getVerification().getStatus());
        assertFalse(response.getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
    }


    @Test
    void verifyIdentityWithInvalidNin() throws MeedlException {
        ninIdentityVerification.setNin("12345678901");
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        log.info("......{}", response);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL_RECORD_NOT_FOUND.getCode(), response.getResponseCode());
    }


    @Test
    void verifyIdentityWhenImageIsNotPosition() throws MeedlException {
        ninIdentityVerification.setImageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027712/ez15xfsdj3whhd5kwscs.jpg");
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertFalse(response.getFaceData().isFaceVerified());
        assertEquals(PremblyVerificationMessage.PREMBLY_FACE_CONFIRMATION.getMessage(), response.getFaceData().getMessage());
    }


    @Test
    void verifyIdentityWhenBalanceIsInsufficient() throws MeedlException {
        PremblyNinResponse insufficientBalanceResponse = PremblyNinResponse.builder()
                .responseCode(PremblyResponseCode.INSUFFICIENT_WALLET_BALANCE.getCode())
                .verificationCallSuccessful(false)
                .build();

        when(premblyAdapter.verifyIdentity(ninIdentityVerification))
                .thenReturn(insufficientBalanceResponse);

        PremblyNinResponse response = (PremblyNinResponse) premblyAdapter.verifyIdentity(ninIdentityVerification);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.INSUFFICIENT_WALLET_BALANCE.getCode(), response.getResponseCode());
        assertFalse(response.isVerificationCallSuccessful());

        verify(premblyAdapter, times(1)).verifyIdentity(ninIdentityVerification);
    }
    @Test
    void verifyLivelinessYTest(){
        PremblyLivelinessResponse livelinessResponse = (PremblyLivelinessResponse) identityVerificationOutputPort.verifyLiveliness(livelinessVerification);
        assertNotNull(livelinessResponse);
        log.info("Response...{}",livelinessResponse);
        assertTrue(livelinessResponse.isStatus());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(),livelinessResponse.getResponseCode());
        assertTrue(livelinessResponse.getVerification().isValidIdentity());
    }
    @Test
    void verifyIdentityWithNullIdentityVerification(){
        assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(null));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyIdentityWithEmptyIdentityId(String nin)  {
        ninIdentityVerification.setNin(nin);
        MeedlException exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyIdentity(ninIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyIdentityWithNullIdentityImage(String url) {
        ninIdentityVerification.setImageUrl(url);
        MeedlException exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyIdentity(ninIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());

    }
    @Test
    void verifyIdentityWithValidBvnAndValidImage() throws MeedlException {
        PremblyBvnResponse response = (PremblyBvnResponse) identityVerificationOutputPort.verifyBvn(bvnIdentityVerification);
        log.info("Prembly {}",response);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getData().getFaceData().getResponseCode());
        assertTrue(response.getData().getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
        assertEquals(bvnIdentityVerification.getBvn(), response.getData().getBvn());
    }
    @Test
    void verifyIdentityWithValidBvnAndImageThatDoesNotMatch() throws MeedlException {
        bvnIdentityVerification.setImageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732042468/gi2ppo8hsivajcn74idz.jpg");
        PremblyBvnResponse response = (PremblyBvnResponse) identityVerificationOutputPort.verifyBvn(bvnIdentityVerification);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL_RECORD_NOT_FOUND.getCode(), response.getData().getFaceData().getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyVerificationMessage.VERIFIED.getMessage(), response.getVerification().getStatus());
        assertFalse(response.getData().getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
    }
    @Test
    void verifyIdentityWithNonExistingBvn() throws MeedlException {
        bvnIdentityVerification.setBvn("12345678908");
        PremblyResponse response = identityVerificationOutputPort.verifyBvn(bvnIdentityVerification);
        log.info("{}", response);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL_RECORD_NOT_FOUND.getCode(), response.getResponseCode());
    }
    @Test
    void verifyBvnIdentityWithNullIdentityVerification() {
        MeedlException exception = assertThrows(
                MeedlException.class,
                () -> identityVerificationOutputPort.verifyBvn(null)
        );
//        assertEquals(IdentityMessage.IDENTITY_SHOULD_NOT_BE_NULL.getMessage(), exception.getMessage());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyIdentityWithEmptyIdentityNumber(String bvn) {
        bvnIdentityVerification.setBvn(bvn);
        MeedlException  exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyBvn(bvnIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());
    }
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyBvnIdentityWithInvalidIdentityImage(String url) {
        bvnIdentityVerification.setImageUrl(url);
        MeedlException exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyBvn(bvnIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());

    }

    @Test
    void verifyBvnIdentityWhenBalanceIsInsufficient() throws MeedlException {
        PremblyBvnResponse insufficientBalanceResponse = PremblyBvnResponse.builder()
                .responseCode(PremblyResponseCode.INSUFFICIENT_WALLET_BALANCE.getCode())
                .verificationCallSuccessful(false)
                .build();

        when(premblyAdapter.verifyBvn(bvnIdentityVerification))
                .thenReturn(insufficientBalanceResponse);

        PremblyResponse response =premblyAdapter.verifyBvn(bvnIdentityVerification);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.INSUFFICIENT_WALLET_BALANCE.getCode(), response.getResponseCode());
        assertFalse(response.isVerificationCallSuccessful());

        verify(premblyAdapter, times(1)).verifyBvn(bvnIdentityVerification);
    }

    //TODO check previous existing tests


    @Test
    void verifyIdentityWithNullIdentityId(){
        identityVerification.setIdentityId(null);
        assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityId(){
       identityVerification.setIdentityId(StringUtils.EMPTY);
       assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithNullIdentityImage(){
       identityVerification.setImageUrl(null);
       assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

    @Test
    void verifyIdentityWithEmptyIdentityImage(){
       identityVerification.setImageUrl(StringUtils.EMPTY);
       assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(identityVerification));
    }

}