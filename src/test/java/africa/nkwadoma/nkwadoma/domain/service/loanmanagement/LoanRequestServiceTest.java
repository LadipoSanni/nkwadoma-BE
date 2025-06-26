package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.input.meedlnotification.MeedlNotificationUsecase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.*;
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
    //TODO COMING BAck FOR SERVICE TEST

    @InjectMocks
    private LoanRequestService loanRequestService;
    @Mock
    private LoanRequestOutputPort loanRequestOutputPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    @Mock
    private LoaneeUseCase loaneeUseCase;
    @Mock
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    @Mock
    private LoanOfferUseCase loanOfferUseCase;
    private LoanRequest loanRequest;
    private LoanOffer loanOffer;
    private List<LoaneeLoanBreakdown> loaneeLoanBreakdowns;
    private LoanProduct loanProduct;
    @Mock
    private LoaneeEmailUsecase sendLoaneeEmailUsecase;
    @Mock
    private LoanRequestMapper loanRequestMapper;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private MeedlNotificationUsecase meedlNotificationUsecase;
    private String testId = "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f";


    @BeforeEach
    void setUp() {
        UserIdentity userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).alternateEmail("alt276@example.com").
                alternatePhoneNumber("0986564534").alternateContactAddress("10, Onigbagbo Street, Mushin, Lagos State").
                createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").bvn("587907453").isIdentityVerified(true).build();

        LoaneeLoanDetail loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        Loanee loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);
        loanee.setOnboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS);
        LoaneeLoanBreakdown loaneeLoanBreakdown =
                TestData.createTestLoaneeLoanBreakdown("1886df42-1f75-4d17-bdef-e0b016707885");
        loaneeLoanBreakdowns = List.of(loaneeLoanBreakdown);

        loanProduct = TestData.buildTestLoanProduct();

        loanRequest = TestData.buildLoanRequest(testId);
        loanRequest.setLoanProductId(loanProduct.getId());
        loanRequest.setLoanee(loanee);

        loanOffer = TestData.buildLoanOffer(loanRequest, loanee);
        loanOffer.setId("9284b721-fd60-4cd9-b6dc-5ef416d70093");
    }

    @Test
    void viewLoanRequestById() {
        try {
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(loanRequest);
            when(loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(loanRequest.getLoaneeId())).thenReturn(loaneeLoanBreakdowns);
            loanRequest.getLoanee().setCreditScore(0);
            when(loaneeUseCase.viewLoaneeDetails(loanRequest.getLoaneeId(), testId)).thenReturn(loanRequest.getLoanee());
            LoanRequest retrievedLoanRequest = loanRequestService.viewLoanRequestById(loanRequest, testId);

            verify(loanRequestOutputPort, times(1)).findById(loanRequest.getId());
            verify(loaneeUseCase, times(1)).viewLoaneeDetails(loanRequest.getLoaneeId(), testId);
            assertNotNull(retrievedLoanRequest);
            assertNotNull(retrievedLoanRequest.getLoaneeLoanBreakdowns());
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    void viewNullLoanRequest() {
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(null, testId));
    }

    @Test
    void viewLoanRequestWithNullId() {
        loanRequest.setId(null);
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest, testId));
    }
    @ParameterizedTest
    @ValueSource(strings = {"36470395798", "sjgbnsvkh"})
    void viewLoanRequestWithNonUUID(String id) {
        loanRequest.setId(id);
        assertThrows(MeedlException.class, ()-> loanRequestService.viewLoanRequestById(loanRequest, id));
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
    void viewAllLoanRequestsByOrganizationId() {
        Page<LoanRequest> loanRequests = Page.empty();
        try {
            loanRequest.setOrganizationId("b95805d1-2e2d-47f8-a037-7bcd264914fc");
            when(loanRequestOutputPort.viewAll(loanRequest.getOrganizationId(), 0, 10)).
                    thenReturn(new PageImpl<>(List.of(loanRequest)));
            loanRequests = loanRequestService.viewAllLoanRequests(loanRequest);

        verify(loanRequestOutputPort, times(1)).
                viewAll(loanRequest.getOrganizationId(),0, 10);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }

        assertNotNull(loanRequests.getContent());
        assertTrue(loanRequests.getContent().stream().map(LoanRequest::getLoanee).findFirst().isPresent());
    }

    @Test
    void approveLoanRequest() {
        try {
            Loanee loanee = Loanee.builder().userIdentity(UserIdentity.builder().build()).build();
            // Setup stubs
            LoanRequest build = LoanRequest.builder().id(testId).isVerified(true)
                    .onboardingMode(OnboardingMode.EMAIL_REFERRED).loanProductId(testId)
                    .status(LoanRequestStatus.NEW).loanAmountApproved(BigDecimal.valueOf(5000))
                    .loanRequestDecision(LoanDecision.ACCEPTED).loanAmountRequested(BigDecimal.valueOf(5000))
                    .loaneeId(testId).userIdentity(UserIdentity.builder().firstName("first name").lastName("hshsh")
                            .email("email@gmail.com").build()).loanee(loanee).build();
            when(loanRequestOutputPort.findById(anyString())).thenReturn(build);
            when(loanProductOutputPort.findById(build.getLoanProductId())).thenReturn(loanProduct);
            when(loanOfferUseCase.createLoanOffer(any())).thenReturn(loanOffer);
            when(loanRequestMapper.updateLoanRequest(any(), any())).thenReturn(build);
            when(loanRequestOutputPort.save(any())).thenReturn(build);
            when(organizationIdentityOutputPort.findOrganizationByName(any()))
                    .thenReturn(Optional.of(OrganizationIdentity.builder().id(testId).build()));
            when(loanMetricsOutputPort.findByOrganizationId(anyString()))
                    .thenReturn(Optional.of(new LoanMetrics()));
            when(loanMetricsOutputPort.save(any())).thenReturn(new LoanMetrics());
            when(userIdentityOutputPort.findById(build.getActorId()))
                    .thenReturn(new UserIdentity());


            LoanRequest response = loanRequestService.respondToLoanRequest(build);
            assertNotNull(response);
            assertEquals(LoanRequestStatus.APPROVED, response.getStatus());
            assertEquals(new BigDecimal("5000"), response.getLoanAmountApproved());
            verify(meedlNotificationUsecase).sendNotification(any(MeedlNotification.class));


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
    void approveLoanRequestWithLoanAmountApprovedGreaterThanAmountRequested() throws MeedlException {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);
        loanRequest.setOnboardingMode(OnboardingMode.FILE_UPLOADED_FOR_DISBURSED_LOANS);
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(700000000));
        loanRequest.setLoanAmountRequested(BigDecimal.valueOf(7000000));
        when(loanRequestOutputPort.findById(anyString())).thenReturn(loanRequest);
        assertThrows(MeedlException.class, ()-> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void approveLoanRequestThatHasBeenApproved() throws MeedlException {
        loanRequest.setLoanProductId(loanRequest.getLoanProductId());
        loanRequest.setId(loanRequest.getId());
        loanRequest.setStatus(LoanRequestStatus.APPROVED);
        loanRequest.setLoanAmountApproved(BigDecimal.valueOf(700000));
        loanRequest.setLoanRequestDecision(LoanDecision.ACCEPTED);

        when(loanRequestOutputPort.findById(anyString())).thenReturn(loanRequest);
        assertThrows(MeedlException.class, () -> loanRequestService.respondToLoanRequest(loanRequest));
    }

    @Test
    void cannotApproveALoanRequestOfALoaneeThatIsNotVerified(){
        loanRequest.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);
        try {
            when(loanRequestOutputPort.findById(loanRequest.getId()))
                    .thenReturn(loanRequest);
            assertThrows(MeedlException.class, () -> loanRequestService.respondToLoanRequest(loanRequest));
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
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
            when(loanRequestOutputPort.findById(loanRequest.getId())).thenReturn(loanRequest);
            when(loanRequestOutputPort.save(any())).thenReturn(loanRequest);
            when(userIdentityOutputPort.findById(loanRequest.getActorId()))
                    .thenReturn(new UserIdentity());
            approvedLoanRequest = loanRequestService.respondToLoanRequest(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(approvedLoanRequest);
        assertEquals(LoanRequestStatus.DECLINED, approvedLoanRequest.getStatus());
        assertEquals("I just don't want the loan offer", approvedLoanRequest.getDeclineReason());
    }
}