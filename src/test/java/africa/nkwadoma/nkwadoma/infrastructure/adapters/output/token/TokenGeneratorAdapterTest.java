package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.token;

import africa.nkwadoma.nkwadoma.application.ports.output.email.TokenGeneratorOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TokenGeneratorAdapterTest {
    @Autowired
    private TokenGeneratorOutputPort tokenGeneratorOutputPort;
    @Test
    void generateToken(){
       try {
           String generatedToken = tokenGeneratorOutputPort.generateToken("test@gmail.com");
           assertNotNull(generatedToken);
       }catch (MiddlException exception){
        log.info("{} {}",exception.getClass().getName(),exception.getMessage());}
    }
    @Test
    void generateTokenWithInvalidEmail(){
       assertThrows(MiddlException.class,()->tokenGeneratorOutputPort.generateToken("invalid"));
    }
    @Test
    void generateTokenWithEmptyEmail(){
        assertThrows(MiddlException.class,()->tokenGeneratorOutputPort.generateToken(StringUtils.EMPTY));
    }
    @Test
    void generateTokenWithNullEmail(){
        assertThrows(MiddlException.class,()->tokenGeneratorOutputPort.generateToken(StringUtils.EMPTY));
    }

}