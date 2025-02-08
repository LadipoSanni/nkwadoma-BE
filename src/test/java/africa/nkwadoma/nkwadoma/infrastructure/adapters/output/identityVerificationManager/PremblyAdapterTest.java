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
import africa.nkwadoma.nkwadoma.infrastructure.utilities.ImageConverter;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.TokenUtils;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    private ImageConverter base64Converter;


    @BeforeEach
    void setUp() {
        bvnIdentityVerification = TestData.createTestIdentityVerification("12345678903", "12345678903");
        ninIdentityVerification = TestData.createTestIdentityVerification("12345678903", "12345678903");
    }

//    @Test
    void verifyIdentityWithValidNinAndValidImage() throws MeedlException {
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        log.info("Response in test: {}",response);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getFaceData().getResponseCode());
        assertTrue(response.getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
        assertEquals(ninIdentityVerification.getDecryptedNin(), response.getNinData().getNin());
    }
//    @Test
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
//    @Test
    void verifyIdentityWithValidBvn() throws MeedlException {
        bvnIdentityVerification.setDecryptedBvn("28393497842");
        PremblyBvnResponse response = (PremblyBvnResponse) identityVerificationOutputPort.verifyBvn(bvnIdentityVerification);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyVerificationMessage.VERIFIED.getMessage(), response.getVerification().getStatus());
    }


//    @Test
    void verifyIdentityWithInvalidNin() throws MeedlException {
        ninIdentityVerification.setDecryptedNin("12345678901");
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        log.info("......{}", response);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL_RECORD_NOT_FOUND.getCode(), response.getResponseCode());
    }


//    @Test
    void verifyIdentityWhenImageIsNotPosition() throws MeedlException {
        ninIdentityVerification.setImageUrl("https://res.cloudinary.com/drhrd1xkn/image/upload/v1732027712/ez15xfsdj3whhd5kwscs.jpg");
        PremblyNinResponse response = (PremblyNinResponse) identityVerificationOutputPort.verifyIdentity(ninIdentityVerification);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertFalse(response.getFaceData().isFaceVerified());
        assertEquals(PremblyVerificationMessage.PREMBLY_FACE_CONFIRMATION.getMessage(), response.getFaceData().getMessage());
    }


//    @Test
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
//    @Test
    void verifyLivelinessTest(){
        PremblyLivelinessResponse livelinessResponse = (PremblyLivelinessResponse) identityVerificationOutputPort.verifyLiveliness(bvnIdentityVerification);
        assertNotNull(livelinessResponse);
        log.info("Response...{}",livelinessResponse);
        assertTrue(livelinessResponse.isStatus());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(),livelinessResponse.getResponseCode());
        assertTrue(livelinessResponse.getVerification().isValidIdentity());
    }
//    @Test
    void verifyIdentityWithNullIdentityVerification(){
        assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(null));
    }

//    @ParameterizedTest
//    @NullSource
//    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyIdentityWithEmptyIdentityId(String nin)  {
        ninIdentityVerification.setDecryptedNin(nin);
        MeedlException exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyIdentity(ninIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());
    }

//    @ParameterizedTest
//    @NullSource
//    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyIdentityWithNullIdentityImage(String url) {
        ninIdentityVerification.setImageUrl(url);
        MeedlException exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyIdentity(ninIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());

    }
//    @Test
    void verifyIdentityWithValidBvnAndValidImage() throws MeedlException {
        log.info("{}", bvnIdentityVerification);
        bvnIdentityVerification.setImageUrl("https://res/dhhhqruoy/image/upload/v1732776176/meedl/mn.jpg");
        bvnIdentityVerification.setDecryptedBvn("12345678909");
        bvnIdentityVerification.setDecryptedNin(null);

        PremblyBvnResponse response = (PremblyBvnResponse) identityVerificationOutputPort.verifyBvnLikeness(bvnIdentityVerification);
        log.info("Prembly bvn test : {}",response);
        assertNotNull(response);
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getResponseCode());
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL.getCode(), response.getData().getFaceData().getResponseCode());
        assertTrue(response.getData().getFaceData().isFaceVerified());
        assertTrue(response.getVerification().isValidIdentity());
        assertEquals(bvnIdentityVerification.getDecryptedBvn(), response.getData().getBvn());
    }
//    @Test
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
//    @Test
    void verifyIdentityWithNonExistingBvn() throws MeedlException {
        bvnIdentityVerification.setDecryptedBvn("12345678908");
        PremblyResponse response = identityVerificationOutputPort.verifyBvn(bvnIdentityVerification);
        log.info("{}", response);
        assertNotNull(response);
        assertTrue(response.isVerificationCallSuccessful());
        assertEquals(PremblyResponseCode.SUCCESSFUL_RECORD_NOT_FOUND.getCode(), response.getResponseCode());
    }
//    @Test
    void verifyBvnIdentityWithNullIdentityVerification() {
        MeedlException exception = assertThrows(
                MeedlException.class,
                () -> identityVerificationOutputPort.verifyBvn(null)
        );
//        assertEquals(IdentityMessage.IDENTITY_SHOULD_NOT_BE_NULL.getMessage(), exception.getMessage());

    }

//    @ParameterizedTest
//    @NullSource
//    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyIdentityWithEmptyIdentityNumber(String bvn) {
        bvnIdentityVerification.setDecryptedBvn(bvn);
        MeedlException  exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyBvn(bvnIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());
    }
//    @ParameterizedTest
//    @NullSource
//    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void verifyBvnIdentityWithInvalidIdentityImage(String url) {
        bvnIdentityVerification.setImageUrl(url);
        MeedlException exception =
                assertThrows(MeedlException.class, () -> identityVerificationOutputPort.verifyBvn(bvnIdentityVerification));
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(),exception.getMessage());

    }

//    @Test
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

//    @Test
    void verifyIdentityWithNullIdentityImage(){
        bvnIdentityVerification.setImageUrl(null);
        assertThrows(MeedlException.class, ()-> identityVerificationOutputPort.verifyIdentity(bvnIdentityVerification));
    }
}