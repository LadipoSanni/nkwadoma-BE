package africa.nkwadoma.nkwadoma.domain.service.identity;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
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
    NextOfKinOutputPort nextOfKinOutputPort;
    @Mock
    LoaneeOutputPort loaneeOutputPort;

    private NextOfKin nextOfKin;
    private UserIdentity userIdentity;
    private Loanee loanee;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;

    @BeforeEach
    void setUp() {
        userIdentity = TestData.createTestUserIdentity("test@example.com", "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f");
        loanee = TestData.createTestLoanee(userIdentity, LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                initialDeposit(BigDecimal.valueOf(3000000.00)).build());
        nextOfKin = TestData.createNextOfKinData(userIdentity);
    }


    @Test
    void saveAdditionalDetails() {
        try {
            when(userIdentityOutputPort.findById(userIdentity.getId())).thenReturn(userIdentity);
            when(nextOfKinOutputPort.save(nextOfKin)).thenReturn(nextOfKin);
            NextOfKin savedNextOfKin = nextOfKinService.saveAdditionalDetails(nextOfKin);

            assertNotNull(savedNextOfKin);
            verify(nextOfKinOutputPort, times(1)).save(nextOfKin);
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

    @Test
    void saveNullUserDetails() {
//        nextOfKin.getLoanee().setUserIdentity(null);
        assertThrows(MeedlException.class, () -> nextOfKinService.saveAdditionalDetails(null));
    }
}