package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
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
    private TokenUtils tokenUtils;
    public static final String ENCRYPTED_DATA = "etlGGJ4BSGNxBkqfv3rPqw==";
    public static final String DECRYPTED_DATA = "93289238223";

    @Test
    void generateToken(){
        try {
            String generatedToken = tokenUtils.generateToken("test@gmail.com");
            log.info("{}",generatedToken);
            assertNotNull(generatedToken);
        }catch (MeedlException exception){
            log.info("{} {}",exception.getClass().getName(),exception.getMessage());}
    }
    @Test
    void generateTokenWithInvalidEmail(){
        assertThrows(MeedlException.class,()-> tokenUtils.generateToken("invalid"));
    }
    @Test
    void generateTokenWithEmptyEmail(){
        assertThrows(MeedlException.class,()-> tokenUtils.generateToken(StringUtils.EMPTY));
    }
    @Test
    void generateTokenWithNullEmail(){
        assertThrows(MeedlException.class,()-> tokenUtils.generateToken(StringUtils.EMPTY));
    }
    @Test
    void testValidDecryption() throws Exception {
        String result = tokenUtils.decryptAES(ENCRYPTED_DATA);
        assertEquals(DECRYPTED_DATA, result, "Decrypted output does not match expected value.");
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE,  "INVALID_BASE64"})
    void testInvalidBase64Data(String emptyData) {
        assertThrows(MeedlException.class, () -> tokenUtils.decryptAES(emptyData), "Should throw an exception for empty encrypted data.");
    }
    @Test
    void testNullEncryptedData() {
        assertThrows(MeedlException.class, () -> tokenUtils.decryptAES(null), "Should throw an exception for null encrypted data.");
    }

    @Test
    void testEncryptData() {
        try {
            String encryptedAES = tokenUtils.encryptAES("22167379603");
            log.info("nin {}",encryptedAES);
            String encryptedAES1 = tokenUtils.encryptAES("50576263862");
            log.info("bvn {}",encryptedAES1);
            assertNotNull(encryptedAES);
            assertEquals(ENCRYPTED_DATA, encryptedAES);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }

}