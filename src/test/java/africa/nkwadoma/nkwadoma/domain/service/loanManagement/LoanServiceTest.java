package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @InjectMocks
    private LoanService loanService;
    @Mock
    private LoanReferralOutputPort loanReferralOutputPort;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    @Mock
    private LoanRequestMapper loanRequestMapper;
    @Mock
    private LoanOfferOutputPort loanOfferOutputPort;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;
    private LoanProduct loanProduct;
    private LoanOffer loanOffer;

    @BeforeEach
    void setUp() {
        UserIdentity userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).alternateEmail("alt276@example.com").
                alternatePhoneNumber("0986564534").alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();

        LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                initialDeposit(BigDecimal.valueOf(3000000.00)).build();
        Loanee loanee = Loanee.builder().id("b1b832a2-5f73-46d8-a073-e5d812304a4b").userIdentity(userIdentity).
                cohortId("3a6d1124-1349-4f5b-831a-ac269369a90f").createdBy(userIdentity.getCreatedBy()).
                loaneeLoanDetail(loaneeLoanDetail).build();

        loanReferral = LoanReferral.builder().id("3a6d1124-1349-4f5b-831a-ac269369a90f").loanee(loanee).
                loanReferralStatus(LoanReferralStatus.ACCEPTED).build();

        Vendor vendor = new Vendor();
        loanProduct = new LoanProduct();
        loanProduct.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanProduct.setName("Test Loan Product - unit testing within application");
        loanProduct.setMandate("Test: A new mandate for test");
        loanProduct.setSponsors(List.of("Mark", "Jack"));
        loanProduct.setObligorLoanLimit(new BigDecimal("100"));
        loanProduct.setTermsAndCondition("Test: A new loan for test and terms and conditions");
        loanProduct.setLoanProductSize(new BigDecimal("1000000"));
        loanProduct.setPageSize(10);
        loanProduct.setPageNumber(0);
        loanProduct.setVendors(List.of(vendor));

        loanRequest = new LoanRequest();
        loanRequest.setId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanRequest.setLoanProductId(loanProduct.getId());
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(500000));
        loanRequest.setLoanRequestDecision("Approved by PM");
        loanRequest.setLoanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested());
        loanRequest.setStatus(LoanRequestStatus.NEW);
        loanRequest.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
        loanRequest.setReferredBy("Brown Hills Institute");
        loanee.setLoaneeLoanDetail(loaneeLoanDetail);
        loanRequest.setLoanee(loanee);
        loanRequest.setDateTimeApproved(LocalDateTime.now());

        loanOffer = new LoanOffer();
        loanOffer.setDateTimeOffered(LocalDateTime.now());
        loanOffer.setLoanRequest(loanRequest);
        loanOffer.setLoanOfferStatus(LoanOfferStatus.OFFERED);
        loanOffer.setLoanee(loanee);
    }


    @Test
    void viewLoanReferral() {
        LoanReferral foundLoanReferral = null;
        try {
            when(loanReferralOutputPort.findLoanReferralById(loanReferral.getId()))
                    .thenReturn(Optional.ofNullable(loanReferral));
            foundLoanReferral = loanService.viewLoanReferral(loanReferral);

            verify(loanReferralOutputPort, times(1)).
                    findLoanReferralById(foundLoanReferral.getId());
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }

        assertNotNull(foundLoanReferral);
    }

    @Test
    void viewLoanReferralWithNullInput() {
        assertThrows(MeedlException.class, () -> loanService.viewLoanReferral(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"     96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f",
            "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f      ",
            "    96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f   "}
    )
    void viewLoanReferralWithTrailingAndLeadingSpaces(String loanReferralId) {
        LoanReferral foundLoanReferral = null;
        try {
            loanReferral.setId(loanReferralId);
            when(loanReferralOutputPort.findLoanReferralById(loanReferral.getId().trim()))
                    .thenReturn(Optional.ofNullable(loanReferral));
            foundLoanReferral = loanService.viewLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertNotNull(foundLoanReferral);
    }

    @Test
    void viewLoanReferralWithNullId() {
        loanReferral.setId(null);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewLoanReferralByIdWithSpaces(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid id", "89954"})
    void viewLoanReferralByNonUUID(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @Test
    void createLoanRequest() {
        try {
            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
            LoanRequest createdLoanRequest = loanService.createLoanRequest(loanRequest);

            verify(loanRequestOutputPort, times(1)).save(loanRequest);
            assertNotNull(createdLoanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @Test
    void createLoanRequestWithNullLoanReferralStatus() {
        loanRequest.setLoanReferralStatus(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNonAcceptedLoanReferralStatus() {
        loanRequest.setLoanReferralStatus(LoanReferralStatus.DECLINED);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(null));
    }

    @Test
    void createLoanRequestWithNullLoanee() {
        loanRequest.setLoanee(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoanAmountRequested() {
        loanRequest.setLoanAmountRequested(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoanRequestStatus() {
        loanRequest.setStatus(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void createLoanRequestWithNullLoaneeLoanDetail() {
        loanRequest.getLoanee().setLoaneeLoanDetail(null);
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(loanRequest));
    }

    @Test
    void acceptLoanReferral() {
        LoanReferral referral = null;
        try {
            when(loanReferralOutputPort.findLoanReferralById(loanReferral.getId())).thenReturn(Optional.of(loanReferral));
            when(loanRequestMapper.mapLoanReferralToLoanRequest(loanReferral)).thenReturn(loanRequest);
            when(loanReferralOutputPort.saveLoanReferral(loanReferral)).thenReturn(loanReferral);
            referral = loanService.respondToLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertNotNull(referral);
        assertEquals(LoanReferralStatus.AUTHORIZED, referral.getLoanReferralStatus());
    }

    @Test
    void acceptLoanReferralWithNullLoaneeAdditionalDetails() {
        loanReferral.getLoanee().getUserIdentity().setAlternateContactAddress(null);
        loanReferral.getLoanee().getUserIdentity().setAlternateEmail(null);
        loanReferral.getLoanee().getUserIdentity().setAlternatePhoneNumber(null);
        assertThrows(MeedlException.class, () -> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void acceptNullLoanReferral() {
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(null));
    }

    @Test
    void acceptLoanReferralWithNullLoanReferralId() {
        loanReferral.setId(null);
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void viewAllLoanRequests() {
        try {
            when(loanRequestOutputPort.viewAll(0, 10)).
                    thenReturn(new PageImpl<>(List.of(loanRequest)));
            Page<LoanRequest> loanRequests = loanService.viewAllLoanRequests(loanRequest);

            verify(loanRequestOutputPort, times(1)).viewAll(0, 10);
            assertNotNull(loanRequests.getContent());
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    void approveLoanRequest() {
        LoanRequest approvedLoanRequest = new LoanRequest();
        try {
            loanRequest.setStatus(LoanRequestStatus.APPROVED);
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(loanRequest);
            when(loanRequestOutputPort.save(any())).thenReturn(loanRequest);
            when(loanProductOutputPort.findById(loanRequest.getLoanProductId())).thenReturn(loanProduct);
            when(loanOfferOutputPort.save(any())).thenReturn(loanOffer);
            approvedLoanRequest = loanService.respondToLoanRequest(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(approvedLoanRequest);
        assertEquals(LoanRequestStatus.APPROVED, approvedLoanRequest.getStatus());
        assertEquals(approvedLoanRequest.getLoanAmountApproved(), BigDecimal.valueOf(500000));
        assertEquals("Approved by PM", approvedLoanRequest.getLoanRequestDecision());
    }

    @Test
    void approveNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanRequest(null));
    }

    @Test
    void approveLoanRequestWithNullLoanRequestId() {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(null);
        loanRequest.setLoanAmountApproved(new BigDecimal("9000"));
        assertThrows(MeedlException.class, () -> loanService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithNullLoanAmountApproved() {
        loanRequest.setLoanAmountApproved(null);
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        MeedlException meedlException = assertThrows(MeedlException.class, () -> loanService.respondToLoanRequest(loanRequest));
        log.info("Exception occurred: {}", meedlException.getMessage());
    }

    @Test
    void approveLoanRequestWithNullLoanProductId() {
        loanRequest.setLoanProductId(null);
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(new BigDecimal("9000"));
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithNonExistingLoanProductId() {
        loanRequest.setLoanProductId("3a6d1124-1349-4f5b-831a-ac269369a90f");
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(new BigDecimal("9000"));
        try {
            when(loanRequestOutputPort.findById(anyString())).thenReturn(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertThrows(MeedlException.class, () -> loanService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithLoanAmountApprovedGreaterThanAmountRequested() {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(700000000));
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestWithStatusThatIsNotNew() {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(700000));
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        try {
            when(loanRequestOutputPort.findById(anyString())).thenReturn(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanRequest(loanRequest));
    }

    @Test
    void declineLoanRequest() {
        loanRequest.setStatus(LoanRequestStatus.DECLINED);
        loanRequest.setDeclineReason("I just don't want the loan offer");
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(70000));

        LoanRequest approvedLoanRequest = new LoanRequest();
        try {
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(loanRequest);
            when(loanRequestOutputPort.save(any())).thenReturn(loanRequest);
            when(loanProductOutputPort.findById(loanRequest.getLoanProductId())).thenReturn(loanProduct);
            approvedLoanRequest = loanService.respondToLoanRequest(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(approvedLoanRequest);
        assertEquals(LoanRequestStatus.DECLINED, approvedLoanRequest.getStatus());
        assertEquals("I just don't want the loan offer", approvedLoanRequest.getDeclineReason());
    }
}
