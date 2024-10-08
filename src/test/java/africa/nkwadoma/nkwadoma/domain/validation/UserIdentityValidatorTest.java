package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MiddlException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserIdentityValidatorTest {


    @Test
    void validateEmailDomainWithNullInviteeEmail(){
        assertThrows(MiddlException.class,()->UserIdentityValidator.validateEmailDomain(null, "me@you.com"));
    }

}