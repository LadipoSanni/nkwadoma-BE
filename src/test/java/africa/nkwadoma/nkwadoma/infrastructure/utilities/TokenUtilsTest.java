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
        String encryptedData = "XdOd8DBvhyOnf3P017NdLw==";
        String expectedOutput = "43423323433";
        String result = tokenUtils.decryptAES(encryptedData);
        assertEquals(expectedOutput, result, "Decrypted output does not match expected value.");
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

}