package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserIdentityValidatorTest {


    @Test
    void validateEmailDomainWithNullInviteeEmail(){
        assertThrows(MeedlException.class,()->UserIdentityValidator.validateEmailDomain(null, "me@you.com"));
    }

}