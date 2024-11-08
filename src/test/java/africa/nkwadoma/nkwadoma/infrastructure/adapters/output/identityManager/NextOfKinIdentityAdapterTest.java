package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NextOfKinIdentityAdapterTest {
    @Autowired
    private NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    private NextOfKin nextOfKin;

    @Test
    void saveNextOfKin() {
        nextOfKin = new NextOfKin();
        nextOfKin.setFirstName("Ahmad");
        nextOfKin.setLastName("Doe");
        nextOfKin.setEmail("ahmad@example.com");
        nextOfKin.setPhoneNumber("0785678901");
        nextOfKin.setNextOfKinRelationship("Brother");
        nextOfKin.setContactAddress("2, Spencer Street, Yaba, Lagos");
        nextOfKin.setLoanee(Loanee.builder().alternateEmail("alt@example.com").
                alternatePhoneNumber("0986564534").alternateContactAddress("10, Onigbaggbo Street, Mushin, Lagos State").build());
        NextOfKin savedNextOfKin = nextOfKinIdentityOutputPort.save(nextOfKin);
        assertNotNull(savedNextOfKin);
    }
}