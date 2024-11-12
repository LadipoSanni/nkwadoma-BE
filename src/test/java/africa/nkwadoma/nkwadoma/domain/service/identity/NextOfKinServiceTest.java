package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.math.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NextOfKinServiceTest {
    @InjectMocks
    private NextOfKinService nextOfKinService;
    @Mock
    NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private Loanee loanee;

    @BeforeEach
    void setUp() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").
                role(IdentityRole.TRAINEE).alternateEmail("alt276@example.com").alternatePhoneNumber("0986564534").
                alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                        initialDeposit(BigDecimal.valueOf(3000000.00)).build()).build();

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
    void createNextOfKin() {
        try {
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            NextOfKin savedNextOfKin = nextOfKinService.createNextOfKin(nextOfKin);

            assertNotNull(savedNextOfKin);
            verify(nextOfKinIdentityOutputPort, times(1)).save(nextOfKin);
            assertEquals(nextOfKin.getFirstName(), savedNextOfKin.getFirstName());
            assertEquals(nextOfKin.getLastName(), savedNextOfKin.getLastName());
            assertEquals(nextOfKin.getEmail(), savedNextOfKin.getEmail());
            assertEquals(nextOfKin.getPhoneNumber(), savedNextOfKin.getPhoneNumber());
            assertEquals(nextOfKin.getNextOfKinRelationship(), savedNextOfKin.getNextOfKinRelationship());
            assertEquals(nextOfKin.getContactAddress(), savedNextOfKin.getContactAddress());
        } catch (MeedlException e) {
            log.error("Exception occurred", e);
        }
    }

    @Test
    void saveNullNextOfKin() {
        assertThrows(MeedlException.class, ()-> nextOfKinService.createNextOfKin(null));
    }

    @Test
    void saveNullUserDetails() {
        nextOfKin.getLoanee().setUserIdentity(null);
        assertThrows(MeedlException.class, () -> nextOfKinService.createNextOfKin(null));
    }
}