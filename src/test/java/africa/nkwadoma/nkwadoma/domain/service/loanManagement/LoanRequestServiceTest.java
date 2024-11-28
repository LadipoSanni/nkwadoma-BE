package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.*;
import africa.nkwadoma.nkwadoma.test.data.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanRequestServiceTest {
    @InjectMocks
    private LoanRequestService loanRequestService;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    @Mock
    private LoanOfferUseCase loanOfferUseCase;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    private LoanService loanService;
    private LoanRequest loanRequest;
    private LoanOffer loanOffer;

    @BeforeEach
    void setUp() {
        UserIdentity userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).alternateEmail("alt276@example.com").
                alternatePhoneNumber("0986564534").alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        LoaneeLoanDetail loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        Loanee loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);
        LoanProduct loanProduct = TestData.buildLoanProduct("Test Loan Product - unit testing within application");

        loanRequest = TestData.buildLoanRequest(loanee, loaneeLoanDetail);
        loanRequest.setLoanProductId(loanProduct.getId());

        loanOffer = TestData.buildLoanOffer(loanRequest, loanee);
    }

//    @Test
//    void createLoanRequest() {
//        try {
//            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
//            LoanRequest createdLoanRequest = loanRequestService.createLoanRequest(loanRequest);
//
//            verify(loanRequestOutputPort, times(1)).save(loanRequest);
//            assertNotNull(createdLoanRequest);
//        } catch (MeedlException e) {
//            log.error("", e);
//        }
//    }
//
//    @Test
//    void createLoanRequestWithNullLoanReferralStatus() {
//        loanRequest.setLoanReferralStatus(null);
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
//    }
//
//    @Test
//    void createLoanRequestWithNonAcceptedLoanReferralStatus() {
//        loanRequest.setLoanReferralStatus(LoanReferralStatus.DECLINED);
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
//    }
//
//    @Test
//    void createNullLoanRequest() {
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(null));
//    }
//
//    @Test
//    void createLoanRequestWithNullLoanee() {
//        loanRequest.setLoanee(null);
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
//    }
//
//    @Test
//    void createLoanRequestWithNullLoanAmountRequested() {
//        loanRequest.setLoanAmountRequested(null);
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
//    }
//
//    @Test
//    void createLoanRequestWithNullLoanRequestStatus() {
//        loanRequest.setStatus(null);
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
//    }
//
//    @Test
//    void createLoanRequestWithNullLoaneeLoanDetail() {
//        loanRequest.getLoanee().setLoaneeLoanDetail(null);
//        assertThrows(MeedlException.class, ()-> loanRequestService.createLoanRequest(loanRequest));
//    }

    @Test
    void viewLoanRequestById() {
        try {
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(Optional.of(loanRequest));
            LoanRequest retrievedLoanRequest = loanRequestService.viewLoanRequestById(loanRequest);

            verify(loanRequestOutputPort, times(1)).findById(loanRequest.getId());
            assertNotNull(retrievedLoanRequest);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    void viewNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(null));
    }

    @Test
    void viewLoanRequestWithNullId() {
        loanRequest.setId(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest));
    }

    @Test
    void viewLoanRequestWithNonExistingId() {
        loanRequest.setId("0d09dd7f-e6ed-49fd-85b8-dfaffcac9ea1");
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest));
    }

    @ParameterizedTest
    @ValueSource(strings = {"36470395798", "sjgbnsvkh"})
    void viewLoanRequestWithNonUUID(String id) {
        loanRequest.setId(id);
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest));
    }

    @Test
    void viewAllLoanRequests() {
        try {
            when(loanRequestOutputPort.viewAll(0, 10)).
                    thenReturn(new PageImpl<>(List.of(loanRequest)));
            Page<LoanRequest> loanRequests = loanRequestService.viewAllLoanRequests(loanRequest);

            verify(loanRequestOutputPort, times(1)).viewAll(0, 10);
            assertNotNull(loanRequests.getContent());
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    void approveLoanRequest() {
        LoanRequest savedLoanRequest;
        try {
            when(loanRequestOutputPort.save(any())).thenReturn(loanRequest);
            savedLoanRequest = loanService.createLoanRequest(loanRequest);

            LoanRequest approvedLoanRequest = new LoanRequest();
            approvedLoanRequest.setLoanProductId(loanRequest.getLoanProductId());
            approvedLoanRequest.setId(savedLoanRequest.getId());
            approvedLoanRequest.setLoanAmountApproved(new BigDecimal("500000"));
            approvedLoanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);

            when(loanRequestOutputPort.findById(approvedLoanRequest.getId())).thenReturn(Optional.of(savedLoanRequest));
            when(loanOfferUseCase.createLoanOffer(any())).thenReturn(loanOffer);
            approvedLoanRequest = loanRequestService.respondToLoanRequest(approvedLoanRequest);

            assertNotNull(approvedLoanRequest);
            assertEquals(LoanRequestStatus.APPROVED, approvedLoanRequest.getStatus());
            assertEquals(approvedLoanRequest.getLoanAmountApproved(), BigDecimal.valueOf(500000));
        } catch (MeedlException e) {
            log.error("Exception occurred saving loan request ", e);
        }

    }

    @Test
    void approveNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanRequestService.respondToLoanRequest(null));
    }

    @Test
    void approveLoanRequestWithNullLoanRequestId() {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(null);
        loanRequest.setLoanAmountApproved(new BigDecimal("9000"));
        assertThrows(MeedlException.class, () -> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithNullLoanAmountApproved() {
        loanRequest.setLoanAmountApproved(null);
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        assertThrows(MeedlException.class, ()-> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithNullLoanProductId() {
        loanRequest.setLoanProductId(null);
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(new BigDecimal("9000"));
        assertThrows(MeedlException.class, ()-> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithNonExistingLoanProductId() {
        loanRequest.setLoanProductId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(new BigDecimal("9000"));
        try {
            when(loanRequestOutputPort.findById(anyString())).thenReturn(Optional.ofNullable(loanRequest));
            when(loanProductOutputPort.findById(anyString())).thenThrow(LoanException.class);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertThrows(MeedlException.class, () -> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithLoanAmountApprovedGreaterThanAmountRequested() {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(700000000));
        try {
            when(loanRequestOutputPort.findById(anyString())).thenReturn(Optional.ofNullable(loanRequest));
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertThrows(MeedlException.class, ()-> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestThatHasBeenApproved() {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(700000));
        loanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);
        try {
            when(loanRequestOutputPort.findById(anyString())).thenReturn(Optional.ofNullable(loanRequest));
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertThrows(MeedlException.class, () -> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void declineLoanRequest() {
        loanRequest.setLoanRequestDecision(LoanDecision.DECLINED);
        loanRequest.setDeclineReason("I just don't want the loan offer");
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(70000));

        LoanRequest approvedLoanRequest = new LoanRequest();
        try {
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(Optional.ofNullable(loanRequest));
            when(loanRequestOutputPort.save(any())).thenReturn(loanRequest);
            approvedLoanRequest = loanRequestService.respondToLoanRequest(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(approvedLoanRequest);
        assertEquals(LoanRequestStatus.DECLINED, approvedLoanRequest.getStatus());
        assertEquals("I just don't want the loan offer", approvedLoanRequest.getDeclineReason());
    }
}