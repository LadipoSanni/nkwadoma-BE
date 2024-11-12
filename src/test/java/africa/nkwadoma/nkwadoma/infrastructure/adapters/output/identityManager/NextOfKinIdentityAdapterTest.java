package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.math.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NextOfKinIdentityAdapterTest {
    @Autowired
    private NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private Loanee loanee;

    @BeforeAll
    void init() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").
                role(IdentityRole.TRAINEE).alternateEmail("alt276@example.com").alternatePhoneNumber("0986564534").
                alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();
        try {
            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
        } catch (MeedlException e) {
            log.error("Exception occurred saving user and loanee", e);
        }
        nextOfKin = new NextOfKin();
        nextOfKin.setFirstName("Ahmad");
        nextOfKin.setLastName("Doe");
        nextOfKin.setEmail("ahmad12@gmail.com");
        nextOfKin.setPhoneNumber("0785678901");
        nextOfKin.setNextOfKinRelationship("Brother");
        nextOfKin.setContactAddress("2, Spencer Street, Yaba, Lagos");
        nextOfKin.setLoanee(loanee);
    }

    @Test
    void saveNextOfKin() {
        NextOfKin savedNextOfKin = null;
        try {
            savedNextOfKin = nextOfKinIdentityOutputPort.save(nextOfKin);
        } catch (MeedlException e) {
            log.error("Exception saving next of kin details", e);
        }
        assertNotNull(savedNextOfKin);
        assertNotNull(savedNextOfKin.getLoanee().getUserIdentity());
    }

    @Test
    void saveNullNextOfKin() {
        assertThrows(MeedlException.class, ()-> nextOfKinIdentityOutputPort.save(null));
    }

    @Test
    void saveNullUserDetails() {
        nextOfKin.getLoanee().setUserIdentity(null);
        assertThrows(MeedlException.class, () -> nextOfKinIdentityOutputPort.save(nextOfKin));
    }

    @AfterAll
    void tearDown() {
        try {
            NextOfKin foundNextOfKin = nextOfKinIdentityOutputPort.findByEmail(nextOfKin.getEmail());
            nextOfKinIdentityOutputPort.deleteNextOfKin(foundNextOfKin.getId());
            Loanee foundLoanee = loaneeOutputPort.findByLoaneeEmail(userIdentity.getEmail());
            loaneeOutputPort.deleteLoanee(foundLoanee.getId());
            userIdentityOutputPort.deleteUserByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.error("Error deleting details", e);
        }
    }
}