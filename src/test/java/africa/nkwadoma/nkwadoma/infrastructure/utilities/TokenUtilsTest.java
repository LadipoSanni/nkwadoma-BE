package africa.nkwadoma.nkwadoma.infrastructure.utilities;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
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
}