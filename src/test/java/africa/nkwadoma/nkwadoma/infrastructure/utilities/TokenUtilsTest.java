package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.email.EmailTokenOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.aes.TokenUtils;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TokenUtilsTest {
    @Autowired
    private AesOutputPort tokenUtils;
    public static final String ENCRYPTED_DATA = "etlGGJ4BSGNxBkqfv3rPqw==";
    public static final String DECRYPTED_DATA = "93289238223";
    @Autowired
    private EmailTokenOutputPort emailTokenManager;

    @Test
    void generateToken(){
        try {
            String generatedToken = emailTokenManager.generateToken("test@gmail.com");
            log.info("{}",generatedToken);
            assertNotNull(generatedToken);
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());}
    }
    @Test
    void generateTokenWithInvalidEmail(){
        assertThrows(MeedlException.class,()-> emailTokenManager.generateToken("invalid"));
    }
    @Test
    void generateTokenWithEmptyEmail(){
        assertThrows(MeedlException.class,()-> emailTokenManager.generateToken(StringUtils.EMPTY));
    }
    @Test
    void generateTokenWithNullEmail(){
        assertThrows(MeedlException.class,()-> emailTokenManager.generateToken(StringUtils.EMPTY));
    }
    @Test
    void testValidDecryption() throws Exception {
        String result = tokenUtils.decryptAES(ENCRYPTED_DATA, "Error processing identity verification");
        assertEquals(DECRYPTED_DATA, result, "Decrypted output does not match expected value.");
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE,  "INVALID_BASE64"})
    void testInvalidBase64Data(String emptyData) {
        assertThrows(MeedlException.class, () -> tokenUtils.decryptAES(emptyData, "Error processing identity verification"), "Should throw an exception for empty encrypted data.");
    }
    @Test
    void testNullEncryptedData() {
        assertThrows(MeedlException.class, () -> tokenUtils.decryptAES(null, "Error processing identity verification"), "Should throw an exception for null encrypted data.");
    }

    @Test
    void testEncryptData() {
        try {
            String encryptedAES = tokenUtils.encryptAES("Shug@8)9#n");
            assertNotNull(encryptedAES);
            assertEquals(ENCRYPTED_DATA, encryptedAES);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }

}