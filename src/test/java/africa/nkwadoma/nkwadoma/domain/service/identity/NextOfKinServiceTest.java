package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NextOfKinServiceTest {
    @InjectMocks
    private NextOfKinService nextOfKinService;
    @Mock
    NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private Loanee loanee;

    @BeforeEach
    void setUp() {
        userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").
                role(IdentityRole.LOANEE).alternateEmail("alt276@example.com").alternatePhoneNumber("0986564534").
                alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        loanee = Loanee.builder().userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").
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
    void saveAdditionalDetails() {
        try {
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            NextOfKin savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);

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
        assertThrows(MeedlException.class, ()-> nextOfKinService.saveAdditionalDetails(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"    Ahmad", "Ahmad   "})
    void saveNextOfKinFirstNameWithTrailingOrLeadingSpaces(String firstName) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.setFirstName(firstName);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"    Doe", "Doe   "})
    void saveNextOfKinLastNameWithTrailingOrLeadingSpaces(String lastName) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.setFirstName(lastName);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"    0785678901", "0785678901   "})
    void saveNextOfKinPhoneNumberWithTrailingOrLeadingSpaces(String phoneNumber) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.setPhoneNumber(phoneNumber);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"    test@example.com", "test@example.com   "})
    void saveNextOfKinEmailWithTrailingOrLeadingSpaces(String email) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.setEmail(email);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   Brother", "Brother   "})
    void saveNextOfKinContactAddressWithTrailingOrLeadingSpaces(String contactAddress) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.setContactAddress(contactAddress);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   2, Spencer Street, Yaba, Lagos", "2, Spencer Street, Yaba, Lagos   "})
    void saveNextOfKinRelationshipWithTrailingOrLeadingSpaces(String nextOfKinRelationship) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.setNextOfKinRelationship(nextOfKinRelationship);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   alt276@example.com", "alt276@example.com   "})
    void saveAlternateEmailWithTrailingOrLeadingSpaces(String alternateEmail) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.getLoanee().getUserIdentity().setAlternateEmail(alternateEmail);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   0986564534", "0986564534   "})
    void saveAlternatePhoneNumberWithTrailingOrLeadingSpaces(String alternatePhoneNumber) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.getLoanee().getUserIdentity().setAlternatePhoneNumber(alternatePhoneNumber);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @ParameterizedTest
    @ValueSource(strings = {"   10, Onigbagbo Street, Mushin, Lagos State", "10, Onigbagbo Street, Mushin, Lagos State   "})
    void saveAlternateContactAddressWithTrailingOrLeadingSpaces(String alternateContactAddress) {
        NextOfKin savedNextOfKin = null;
        try {
            nextOfKin.getLoanee().getUserIdentity().setAlternateContactAddress(alternateContactAddress);
            when(loaneeOutputPort.findByUserId(nextOfKin.getLoanee().getUserIdentity().getId())).thenReturn(Optional.of(loanee));
            when(nextOfKinIdentityOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);
        } catch (MeedlException e) {
            log.error("Failed to save next of kin", e);
        }
        assertNotNull(savedNextOfKin);
    }

    @Test
    void saveNullUserDetails() {
        nextOfKin.getLoanee().setUserIdentity(null);
        assertThrows(MeedlException.class, () -> nextOfKinService.saveAdditionalDetails(null));
    }
}