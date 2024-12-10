package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.identityManager;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
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
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private Loanee loanee;
    private String loaneeLoanDetailId;
    private String userId;

    @BeforeAll
    void init() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();

        try {
            UserIdentity savedUserIdentity = userIdentityOutputPort.save(loanee.getUserIdentity());
            userId = savedUserIdentity.getId();
            LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loanee.getLoaneeLoanDetail());
            loaneeLoanDetailId = savedLoaneeLoanDetail.getId();

            loanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);
            loanee.setUserIdentity(savedUserIdentity);
            loanee = loaneeOutputPort.save(loanee);

            assertNotNull(loanee);
            UserIdentity foundUserIdentity = userIdentityOutputPort.findByEmail(loanee.getUserIdentity().getEmail());
            assertNotNull(foundUserIdentity);

            nextOfKin = new NextOfKin();
            nextOfKin.setFirstName("Ahmad");
            nextOfKin.setLastName("Doe");
            nextOfKin.setEmail("ahmad12@gmail.com");
            nextOfKin.setPhoneNumber("0785678901");
            nextOfKin.setNextOfKinRelationship("Brother");
            nextOfKin.setContactAddress("2, Spencer Street, Yaba, Lagos");
            log.info("Saved User Identity: {}", foundUserIdentity);
            foundUserIdentity.setAlternateEmail("alt276@example.com");
            foundUserIdentity.setAlternatePhoneNumber("0987654321");
            foundUserIdentity.setAlternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State");
            loanee.setUserIdentity(foundUserIdentity);
            nextOfKin.setLoanee(loanee);
        } catch (MeedlException e) {
            log.error("Error saving Loanee details========> {}", e.getMessage());
        }
    }


    @Test
    void saveNextOfKin() {
        NextOfKin savedNextOfKin = null;
        try {
            savedNextOfKin = nextOfKinIdentityOutputPort.save(nextOfKin);
            log.info("Saved next of Kin: {}", savedNextOfKin);
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
            loaneeLoanDetailId = foundLoanee.getLoaneeLoanDetail().getId();
            loaneeOutputPort.deleteLoanee(foundLoanee.getId());
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);
            userIdentityOutputPort.deleteUserByEmail(userIdentity.getEmail());
        } catch (MeedlException e) {
            log.error("Error deleting details", e);
        }
    }
}