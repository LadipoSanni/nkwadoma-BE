package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.exceptions.LoanException;
import africa.nkwadoma.nkwadoma.test.data.TestData;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;

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
    private LoanRequestMapper loanRequestMapper;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    @Mock
    private LoanOutputPort loanOutputPort;
    @Mock
    private LoaneeLoanAccountPersistenceAdapter loaneeLoanAccountOutputPort;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    @Mock
    private IdentityVerificationUseCase verificationUseCase;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private LoanMetricsService loanMetricsUseCase;
    @Mock
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;
    private Loan loan;
    private Loanee loanee;
    private UserIdentity userIdentity;
    private String testId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private LoaneeLoanAccount loaneeLoanAccount;
    private LoanMetrics loanMetrics;
    private OrganizationIdentity organizationIdentity;


    @BeforeEach
    void setUp() {
        organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setId("83f744df-78a2-4db6-bb04-b81545e78e49");
        organizationIdentity.setName("Brown Hills Institute");
        organizationIdentity.setEmail("iamoluchimercy@gmail.com");
        organizationIdentity.setTin("7682-5627");
        organizationIdentity.setRcNumber("RC8789905");
        organizationIdentity.setServiceOfferings(List.of(new ServiceOffering()));
        organizationIdentity.getServiceOfferings().get(0).setIndustry(Industry.EDUCATION);
        organizationIdentity.setPhoneNumber("09876365713");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setWebsiteAddress("rosecouture.org");
        userIdentity = TestData.createTestUserIdentity("test@example.com");
        LoaneeLoanDetail loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW, loanee.getId());

        loanReferral = LoanReferral.builder().id(testId).loanee(loanee).
                loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
        Vendor vendor = TestData.createTestVendor("Large vendor");
        LoanProduct loanProduct = TestData.buildTestLoanProduct("Test Loan Product - unit testing within application", vendor);

        loanRequest = TestData.buildLoanRequest(loanee, loaneeLoanDetail);
        loanRequest.setLoaneeId(loanee.getId());
        loanRequest.setLoanProductId(loanProduct.getId());
        loanRequest.setLoanReferralId(loanReferral.getId());
        loanRequest.setReferredBy(organizationIdentity.getName());
        loanRequest.setCreatedDate(LocalDateTime.now());

        loanMetrics = LoanMetrics.builder()
                .organizationId(organizationIdentity.getId())
                .loanRequestCount(1)
                .build();

        loan = TestData.createTestLoan(loanee);
        loan.setOrganizationId("b95805d1-2e2d-47f8-a037-7bcd264914fc");
        loan.setPageNumber(0);
        loan.setPageSize(10);
    }

    @Test
    void createLoanRequest() {
        try {
            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
            when(organizationIdentityOutputPort.findOrganizationByName(organizationIdentity.getName())).
                    thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanMetricsUseCase.save(any())).thenReturn(loanMetrics);
            LoanRequest createdLoanRequest = loanService.createLoanRequest(loanRequest);

            verify(loanRequestOutputPort, times(1)).save(loanRequest);
            assertNotNull(createdLoanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @Test
    void createNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanService.createLoanRequest(null));
    }

    @Test
    void viewLoanReferral() {
        LoanReferral foundLoanReferral;
        try {
            when(loanReferralOutputPort.findLoanReferralByUserId(
                    loanReferral.getLoanee().getUserIdentity().getId())).thenReturn(List.of(loanReferral));
            when(loanReferralOutputPort.
                    findLoanReferralById(loanReferral.getId())).thenReturn(Optional.ofNullable(loanReferral));
            when(loaneeLoanBreakDownOutputPort.findAllByLoaneeId(anyString())).thenReturn(List.of(TestData.createTestLoaneeLoanBreakdown(testId)));
            foundLoanReferral = loanService.viewLoanReferral(loanReferral);

            verify(loanReferralOutputPort, times(1)).
                    findLoanReferralByUserId(foundLoanReferral.getLoanee().getUserIdentity().getId());
            assertNotNull(foundLoanReferral);
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
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
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
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
    void viewLoanReferralByNonUUID(String id) {
        loanReferral.setId(id);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(loanReferral));
    }

    @Test
    void acceptLoanReferral() {
        LoanReferral referral = null;
        try {
            when(loanReferralOutputPort.findById(loanReferral.getId())).thenReturn(loanReferral);
            when(loanRequestMapper.mapLoanReferralToLoanRequest(loanReferral)).thenReturn(loanRequest);
            when(organizationIdentityOutputPort.findOrganizationByName(organizationIdentity.getName())).
                    thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanReferralOutputPort.save(loanReferral)).thenReturn(loanReferral);
            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
            referral = loanService.respondToLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertNotNull(referral);
        assertEquals(LoanReferralStatus.AUTHORIZED, referral.getLoanReferralStatus());
    }
    @Test
    void acceptNullLoanReferral() {
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(null));
    }

    @Test
    void respondToLoanReferralWithInvalidLoanReferralStatus() {
        loanReferral.setLoanReferralStatus(LoanReferralStatus.REJECTED);
        assertThrows(MeedlException.class, () -> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void declineLoanReferral() {
        loanReferral.setLoanReferralStatus(LoanReferralStatus.DECLINED);
        loanReferral.setReasonForDeclining("I just don't want a loan");
        LoanReferral referral = null;
        try {
            when(loanReferralOutputPort.findById(loanReferral.getId())).thenReturn(loanReferral);
            when(loanReferralOutputPort.save(loanReferral)).thenReturn(loanReferral);
            referral = loanService.respondToLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertNotNull(referral);
        assertEquals(LoanReferralStatus.UNAUTHORIZED, referral.getLoanReferralStatus());
        assertEquals("I just don't want a loan", referral.getReasonForDeclining());
    }

    @Test
    void startLoan() {
        Loan startedLoan = null;
        try {
            when(loaneeOutputPort.findLoaneeById(loan.getLoaneeId())).thenReturn(loanee);
            when(loanOutputPort.save(loan)).thenReturn(loan);
            when(loaneeLoanAccountOutputPort.findByLoaneeId(loanee.getId())).thenReturn(loaneeLoanAccount);
            startedLoan = loanService.startLoan(loan);
        } catch (MeedlException e) {
            log.error("Failed to start loan", e);
        }
        assertNotNull(startedLoan);
        assertEquals(LoanStatus.PERFORMING, startedLoan.getLoanStatus());
        assertEquals(loaneeLoanAccount.getId(), startedLoan.getLoanAccountId());
        assertEquals(loanee.getId(), startedLoan.getLoaneeId());
    }

    @Test
    void startLoanWithNull() {
        assertThrows(MeedlException.class, ()-> loanService.startLoan(null));
    }

    @ParameterizedTest
    @ValueSource(strings={StringUtils.EMPTY, StringUtils.SPACE, "invalid uuid"})
    void startLoanWithInvalidId(String loaneeId) {
        loan.setLoaneeId(loaneeId);
        assertThrows(MeedlException.class, ()-> loanService.startLoan(null));
    }

    @Test
    void acceptLoanReferralWithNullLoanReferralId() {
        loanReferral.setId(null);
        try {
            doThrow(LoanException.class).when(loanReferralOutputPort).findById(loanReferral.getId());
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertThrows(LoanException.class, ()-> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void viewAllLoanInOrganization() {
        Page<Loan> loans = Page.empty();
        try {
            when(loanOutputPort.findAllByOrganizationId(anyString(), anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.viewAllLoansByOrganizationId(loan);
        } catch (MeedlException e) {
            log.error("Error viewing all loans: ", e);
        }

        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
        try {
            verify(loanOutputPort, times(1))
                    .findAllByOrganizationId(loan.getOrganizationId(), 10, 0);
        } catch (MeedlException e) {
            log.error("Error viewing all loans: ", e);
        }
    }

}
