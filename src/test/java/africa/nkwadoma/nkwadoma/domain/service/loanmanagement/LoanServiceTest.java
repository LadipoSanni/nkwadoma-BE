package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeLoanAggregateMapper;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
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
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    @Mock
    private LoanOfferOutputPort loanOfferOutputPort;
    @Mock
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;
    private Loan loan;
    private Loanee loanee;
    private UserIdentity userIdentity;
    private String testId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private LoaneeLoanAccount loaneeLoanAccount;
    private LoanMetrics loanMetrics;
    private OrganizationIdentity organizationIdentity;
    private InvestmentVehicle investmentVehicle;
    private int pageSize = 10;
    private int pageNumber = 0;
    private CohortLoanee cohortLoanee;
    private LoaneeLoanDetail loaneeLoanDetail;
    @Mock
    private CohortOutputPort cohortOutputPort;
    @Mock
    private CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private CohortLoanDetail cohortLoanDetail;
    private Cohort cohort;
    @Mock
    private ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    @Mock
    private OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    private OrganizationLoanDetail organizationLoanDetail;
    private ProgramLoanDetail programLoanDetail;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Mock
    private LoanMapper loanMapper;
    LoanDetailSummary loanDetailSummary;
    @Mock
    private LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;
    private LoaneeLoanAggregate loaneeLoanAggregate;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    @Mock
    private LoaneeLoanAggregateMapper loaneeLoanAggregateMapper;
    private LoanOffer loanOffer;


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
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW, loanee.getId());
        LoanProduct loanProduct = TestData.buildTestLoanProduct();



        loanMetrics = LoanMetrics.builder()
                .organizationId(organizationIdentity.getId())
                .loanRequestCount(1)
                .build();

        loan = TestData.createTestLoan(loanee);
        loan.setOrganizationId("b95805d1-2e2d-47f8-a037-7bcd264914fc");
        loan.setPageNumber(0);
        loan.setPageSize(10);
        loan.setActorId(userIdentity.getId());
        cohort = TestData.createCohortData("elites",testId,testId,List.of(new LoanBreakdown()),testId);

        cohortLoanee = TestData.buildCohortLoanee(loanee,cohort,loaneeLoanDetail,testId);
        loanReferral = LoanReferral.builder().id(testId)
                .loanee(loanee)
                .loanReferralStatus(LoanReferralStatus.ACCEPTED)
                .loaneeUserId(testId)
                .cohortLoanee(cohortLoanee)
                .build();

        loanRequest = TestData.buildLoanRequest(testId);
        loanRequest.setLoaneeId(loanee.getId());
        loanRequest.setId(loanReferral.getId());
        loanRequest.setLoanProductId(loanProduct.getId());
        loanRequest.setReferredBy(organizationIdentity.getName());
        loanRequest.setCreatedDate(LocalDateTime.now());

        investmentVehicle = TestData.buildInvestmentVehicle("vehicle");
        cohortLoanDetail = TestData.buildCohortLoanDetail(cohort);
        organizationLoanDetail = TestData.buildOrganizationLoanDetail(organizationIdentity);
        programLoanDetail = TestData.buildProgramLoanDetail(Program.builder().id(testId).build());

        loanDetailSummary = LoanDetailSummary.builder()
                .totalAmountOutstanding(BigDecimal.valueOf(3000.00))
                .totalAmountReceived(BigDecimal.valueOf(5000.00))
                .totalAmountRepaid(BigDecimal.valueOf(2000.00))
                .build();

        loaneeLoanAggregate = TestData.buildLoaneeLoanAggregate(loanee);
        organizationEmployeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(userIdentity);
        loanOffer = TestData.buildLoanOffer(loanRequest);
    }

    @Test
    void createLoanRequest() {
        try {
            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);
            loanRequest.setReferredBy(organizationIdentity.getName());
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
        LoanReferral foundLoanReferral = new LoanReferral();
        try {
            userIdentity.setId(testId);
            loanReferral.setLoaneeUserId(testId);
            loanReferral.setCohortLoaneeId(testId);
            when(userIdentityOutputPort.findById(testId)).thenReturn(userIdentity);
            when(loanReferralOutputPort.
                    findLoanReferralById(testId)).thenReturn(Optional.ofNullable(loanReferral));
            when(loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(
                    loanReferral.getCohortLoaneeId())).thenReturn(List.of(TestData.createTestLoaneeLoanBreakdown(testId)));
            foundLoanReferral = loanService.viewLoanReferral(testId,testId);
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
        assertEquals(testId, foundLoanReferral.getId());
    }

    @Test
    void viewLoanReferralThatIsntAssignedToMe() {
        UserIdentity userIdentity1 = new UserIdentity();
        userIdentity1.setId("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f");
        try {
            loanReferral.getCohortLoanee().setId(testId);
            when(userIdentityOutputPort.findById(testId)).thenReturn(userIdentity1);
            when(loanReferralOutputPort.
                    findLoanReferralById(testId)).thenReturn(Optional.ofNullable(loanReferral));
            assertThrows(MeedlException.class, () ->  loanService.viewLoanReferral(testId,testId));
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
    }

    @Test
    void viewLoanReferralWithNullInput() {
        assertThrows(MeedlException.class, () -> loanService.viewLoanReferral(null,null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"     96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f",
            "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f      ",
            "    96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f   "}
    )
    void viewLoanReferralWithTrailingAndLeadingSpaces(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(testId,loanReferralId));
    }

    @Test
    void viewLoanReferralWithNullLoanRereralId() {
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(testId,null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewLoanReferralByLoanReferralIdWithSpaces(String loanReferralId) {
        loanReferral.setId(loanReferralId);
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(null,loanReferralId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid id", "89954"})
    void viewLoanReferralByNonUUID(String id) {
        assertThrows(MeedlException.class, ()->loanService.viewLoanReferral(testId,id));
    }

    @Test
    void acceptLoanReferral() {
        LoanReferral referral = null;
        try {
            loanReferral.setLoaneeUserId(testId);
            loanReferral.getCohortLoanee().getLoanee().getUserIdentity().setId(testId);
            loanReferral.getCohortLoanee().getLoanee().getUserIdentity().setIdentityVerified(true);
            when(loanReferralOutputPort.findById(loanReferral.getId())).thenReturn(loanReferral);
            when(cohortOutputPort.save(cohortLoanee.getCohort())).thenReturn(cohortLoanee.getCohort());
            when(loanRequestMapper.mapLoanReferralToLoanRequest(loanReferral)).thenReturn(loanRequest);

            when(loanRequestOutputPort.save(loanRequest)).thenReturn(loanRequest);

            loanRequest.setReferredBy(organizationIdentity.getName());

            when(organizationIdentityOutputPort.findOrganizationByName(loanRequest.getReferredBy())).
                    thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId()))
                    .thenReturn(Optional.ofNullable(loanMetrics));
            when(loanMetricsOutputPort.save(loanMetrics)).thenReturn(loanMetrics);

            cohort.setId(testId);
            when(cohortLoanDetailOutputPort.findByCohortId(cohort.getId())).thenReturn(cohortLoanDetail);
            when(cohortLoanDetailOutputPort.save(cohortLoanDetail)).thenReturn(cohortLoanDetail);
            when(programLoanDetailOutputPort.findByProgramId(cohort.getProgramId())).thenReturn(programLoanDetail);
            when(programLoanDetailOutputPort.save(programLoanDetail)).thenReturn(programLoanDetail);
            when(organizationLoanDetailOutputPort.findByOrganizationId(cohort.getOrganizationId())).thenReturn(organizationLoanDetail);
            when(organizationLoanDetailOutputPort.save(organizationLoanDetail)).thenReturn(organizationLoanDetail);


            when(loanReferralOutputPort.save(loanReferral)).thenReturn(loanReferral);

            cohortLoanee.setReferredBy("Brown Hills Institute");
            loanReferral.setCohortLoanee(cohortLoanee);

            when(organizationIdentityOutputPort.findOrganizationByName(loanReferral.getCohortLoanee().getReferredBy())).
                    thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId()))
                    .thenReturn(Optional.ofNullable(loanMetrics));
            when(loanMetricsOutputPort.save(loanMetrics)).thenReturn(loanMetrics);


            referral = loanService.respondToLoanReferral(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertNotNull(referral);
        assertEquals(LoanReferralStatus.AUTHORIZED, referral.getLoanReferralStatus());
    }

    @Test
    void acceptLoanReferralThatHasAlreadyBeenAccepted() {
        loanReferral.setLoanReferralStatus(LoanReferralStatus.AUTHORIZED);
        try {
            when(loanReferralOutputPort.findById(anyString())).thenReturn(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertThrows(MeedlException.class, () -> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void acceptNullLoanReferral() {
        loanReferral.setLoanReferralStatus(null);
        loanReferral.setCohortLoanee(cohortLoanee);
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void respondToLoanReferralWithInvalidLoanReferralStatus() {
        loanReferral.setLoanReferralStatus(LoanReferralStatus.REJECTED);
        try {
            when(loanReferralOutputPort.findById(anyString())).thenReturn(loanReferral);
        } catch (MeedlException e) {
            log.error(e.getMessage(), e);
        }
        assertThrows(MeedlException.class, () -> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void declineLoanReferral() {
        loanReferral.setLoanReferralStatus(LoanReferralStatus.DECLINED);
        loanReferral.setReasonForDeclining("I just don't want a loan");
        LoanReferral referral = null;
        try {
            loanReferral.setLoaneeUserId(testId);
            loanReferral.getCohortLoanee().getLoanee().getUserIdentity().setId(testId);
            when(loanReferralOutputPort.findById(loanReferral.getId())).thenReturn(loanReferral);
            when(loanReferralOutputPort.save(loanReferral)).thenReturn(loanReferral);

            cohortLoanee.setReferredBy("Brown Hills Institute");
            loanReferral.setCohortLoanee(cohortLoanee);

            when(organizationIdentityOutputPort.findOrganizationByName(loanReferral.getCohortLoanee().getReferredBy())).
                    thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId()))
                    .thenReturn(Optional.ofNullable(loanMetrics));
            when(loanMetricsOutputPort.save(loanMetrics)).thenReturn(loanMetrics);


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
        Loan startedLoan = Loan.builder().
                loanStatus(LoanStatus.PERFORMING).
                loanAccountId(loaneeLoanAccount.getId()).
                loanOfferId(testId).
                loaneeId(loanee.getId()).build();

        LoanOffer loanOffer = new LoanOffer();
        loanOffer.setId(testId);
        loanOffer.setLoaneeResponse(LoanDecision.ACCEPTED);
        loanOffer.setAmountApproved(BigDecimal.valueOf(9000));
        loanOffer.setOrganizationId(testId);
        loanOffer.setCohortId(testId);
        try {
            loanOffer.setLoaneeId(loanee.getId());
            when(loaneeOutputPort.findLoaneeById(anyString())).thenReturn(loanee);
            when(loanOfferOutputPort.findLoanOfferById(loan.getLoanOfferId())).
                    thenReturn(loanOffer);
            when(loaneeLoanAccountOutputPort.findByLoaneeId(anyString())).thenReturn(loaneeLoanAccount);
            when(loanOutputPort.save(any())).thenReturn(startedLoan);
            cohort.setId(testId);

            when(loaneeLoanAggregateOutputPort.findByLoaneeId(loanee.getId())).thenReturn(loaneeLoanAggregate);
            when(loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate)).thenReturn(loaneeLoanAggregate);


            when(loaneeLoanDetailsOutputPort.findByCohortLoaneeId(loanOffer.getCohortLoaneeId()))
                    .thenReturn(loaneeLoanDetail);
            when(loaneeLoanDetailsOutputPort.save(loaneeLoanDetail)).thenReturn(loaneeLoanDetail);
            when(cohortLoanDetailOutputPort.findByCohortId(loanOffer.getCohortId())).thenReturn(cohortLoanDetail);
            when(cohortLoanDetailOutputPort.save(cohortLoanDetail)).thenReturn(cohortLoanDetail);
            when(cohortOutputPort.findCohortById(loanOffer.getCohortId())).thenReturn(cohort);
            when(programLoanDetailOutputPort.findByProgramId(cohort.getProgramId())).thenReturn(programLoanDetail);
            when(programLoanDetailOutputPort.save(programLoanDetail)).thenReturn(programLoanDetail);
            when(organizationLoanDetailOutputPort.findByOrganizationId(cohort.getOrganizationId())).thenReturn(organizationLoanDetail);
            when(organizationLoanDetailOutputPort.save(organizationLoanDetail)).thenReturn(organizationLoanDetail);

            when(organizationIdentityOutputPort.findOrganizationByName(loanee.getReferredBy()))
                    .thenReturn(Optional.ofNullable(organizationIdentity));
            when(loanMetricsOutputPort.findByOrganizationId(organizationIdentity.getId()))
                    .thenReturn(Optional.of(loanMetrics));
            when(loanMetricsOutputPort.save(loanMetrics)).thenReturn(loanMetrics);
            when(investmentVehicleOutputPort.findInvestmentVehicleByLoanOfferId(startedLoan.getLoanOfferId()))
                    .thenReturn(investmentVehicle);
            when(investmentVehicleOutputPort.save(investmentVehicle)).thenReturn(investmentVehicle);
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
        assertThrows(MeedlException.class, ()-> loanService.respondToLoanReferral(loanReferral));
    }

    @Test
    void viewAllLoanInOrganization() {
        loan.setOrganizationId(testId);
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        Page<Loan> loans = Page.empty();

        try {
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.findAllByOrganizationId(anyString(), anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.viewAllLoans(loan);
        } catch (MeedlException e) {
            log.error("Error viewing all loans: ", e);
        }

        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    void viewAllLoan(){
        Page<Loan> loans = Page.empty();
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        try{
            log.info("org id  {}",loan.getOrganizationId());
            loan.setOrganizationId(null);
            loan.setLoaneeId(null);
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.findAllLoan(pageSize,pageNumber))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.viewAllLoans(loan);
        }catch (MeedlException e){
            log.error("Error viewing all loans: {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    void viewAllLoanByLOaneeId(){
        Page<Loan> loans = Page.empty();
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        try{
            loan.setOrganizationId(null);
            loan.setLoaneeId(loanee.getId());
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.findAllLoanDisburedToLoaneeByLoaneeId(loan.getLoaneeId(),loan.getPageSize(),pageNumber))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.viewAllLoans(loan);
        }catch (MeedlException e){
            log.error("Error viewing all loans: {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    void viewAllLoanByLoanee(){
        Page<Loan> loans = Page.empty();
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        loan.setActorId(userIdentity.getId());
        userIdentity.setRole(IdentityRole.LOANEE);
        try{
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.findAllLoanDisburedToLoanee(userIdentity.getId(),pageNumber,pageSize))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.viewAllLoans(loan);
        }catch (MeedlException e){
            log.error("Error viewing all loans: {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    void viewLoaneeDetailsTotalByLoanee(){
        try {
        when(userIdentityOutputPort.findById(testId)).thenReturn(userIdentity);
        when(loaneeLoanDetailsOutputPort.getLoaneeLoanSummary(testId)).thenReturn(loanDetailSummary);
        loanDetailSummary = loanService.viewLoanTotal(testId,null);
    }catch (MeedlException exception){
        log.error(exception.getMessage(), exception);
    }
        assertNotNull(loanDetailSummary);
    }

    @Test
    void viewLoaneeDetailsTotalByMeedlStaff(){
        try {
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        when(userIdentityOutputPort.findById(testId)).thenReturn(userIdentity);
        when(loaneeLoanAggregateOutputPort.getLoanAggregationSummary()).thenReturn(loanDetailSummary);
        loanDetailSummary = loanService.viewLoanTotal(testId,null);
    }catch (MeedlException exception){
        log.error(exception.getMessage(), exception);
    }
        assertNotNull(loanDetailSummary);
    }
    @Test
    void viewLoaneeDetailsTotalByMeedlStaffAndLoaneeId(){
        try {
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        when(userIdentityOutputPort.findById(testId)).thenReturn(userIdentity);
        when(loaneeLoanAggregateOutputPort.findByLoaneeId(testId)).thenReturn(loaneeLoanAggregate);
        when(loaneeLoanAggregateMapper.mapLoaneeLoanAggregateTOLoanDetailSummary(loaneeLoanAggregate)).thenReturn(loanDetailSummary);
        loanDetailSummary = loanService.viewLoanTotal(testId,testId);
    }catch (MeedlException exception){
        log.error(exception.getMessage(), exception);
    }
        assertNotNull(loanDetailSummary);
    }

    @Test
    void viewLoaneeDetailsTotalByByOrganizationStaff(){
        try {
            userIdentity.setRole(IdentityRole.ORGANIZATION_SUPER_ADMIN);
        when(userIdentityOutputPort.findById(testId)).thenReturn(userIdentity);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(anyString())).thenReturn(Optional.ofNullable(organizationEmployeeIdentity));
        when(loaneeLoanDetailsOutputPort.getOrganizationLoanSummary(organizationEmployeeIdentity.getOrganization())).thenReturn(loanDetailSummary);
        loanDetailSummary = loanService.viewLoanTotal(testId,null);
    }catch (MeedlException exception){
        log.error(exception.getMessage(), exception);
    }
        assertNotNull(loanDetailSummary);
    }


    @Test
    void viewLoanReferralForLoanee_Success() throws MeedlException {
        String userId = testId;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        when(loaneeOutputPort.findByUserId(userId)).thenReturn(Optional.of(loanee));
        loanReferral.setId(UUID.randomUUID().toString());
        loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);
        LoanReferral loanReferral2 = LoanReferral.builder()
                .id(UUID.randomUUID().toString())
                .loanee(loanee)
                .loanReferralStatus(LoanReferralStatus.ACCEPTED)
                .loaneeUserId(testId)
                .cohortLoaneeId(cohortLoanee.getId())
                .build();
        Page<LoanReferral> loanReferralsPage = new PageImpl<>(List.of(loanReferral, loanReferral2), pageable, 2);
        when(loanReferralOutputPort.findAllLoanReferralsForLoanee(loanee.getId(), pageNumber, pageSize))
                .thenReturn(loanReferralsPage);

        List<LoaneeLoanBreakdown> breakdowns1 = List.of(TestData.createTestLoaneeLoanBreakdown(cohortLoanee.getId()));
        List<LoaneeLoanBreakdown> breakdowns2 = List.of(TestData.createTestLoaneeLoanBreakdown(cohortLoanee.getId()));
        when(loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(cohortLoanee.getId()))
                .thenReturn(breakdowns1)
                .thenReturn(breakdowns2);
        Page<LoanReferral> result = loanService.viewLoanReferralsForLoanee(userId, pageNumber, pageSize);
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals(breakdowns1, result.getContent().get(0).getLoaneeLoanBreakdowns());
        assertEquals(breakdowns2, result.getContent().get(1).getLoaneeLoanBreakdowns());
        verify(loaneeOutputPort, times(1)).findByUserId(userId);
        verify(loanReferralOutputPort, times(1)).findAllLoanReferralsForLoanee(loanee.getId(), pageNumber, pageSize);
        verify(loaneeLoanBreakDownOutputPort, times(2)).findAllLoaneeLoanBreakDownByCohortLoaneeId(cohortLoanee.getId());
        verifyNoMoreInteractions(loaneeOutputPort, loanReferralOutputPort, loaneeLoanBreakDownOutputPort);
    }

    @Test
    void viewLoanReferralForLoanee_WithMeedlExceptionInBreakdownFetch() throws MeedlException {
        String userId = testId;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        when(loaneeOutputPort.findByUserId(userId)).thenReturn(Optional.of(loanee));
        loanReferral.setId(UUID.randomUUID().toString());
        loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);
        Page<LoanReferral> loanReferralsPage = new PageImpl<>(List.of(loanReferral), pageable, 1);
        when(loanReferralOutputPort.findAllLoanReferralsForLoanee(loanee.getId(), pageNumber, pageSize))
                .thenReturn(loanReferralsPage);

        MeedlException mockException = new MeedlException("Failed to fetch loan breakdowns");
        when(loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(cohortLoanee.getId()))
                .thenThrow(mockException);
        Page<LoanReferral> loanReferrals = loanService.viewLoanReferralsForLoanee(userId, pageNumber, pageSize);

        assertNotNull(loanReferrals);
        assertEquals(1, loanReferrals.getTotalElements());
        assertEquals(1, loanReferrals.getContent().size());
        assertNull(loanReferrals.getContent().get(0).getLoaneeLoanBreakdowns());
        verify(loaneeOutputPort, times(1)).findByUserId(userId);
        verify(loanReferralOutputPort, times(1)).findAllLoanReferralsForLoanee(loanee.getId(), pageNumber, pageSize);
        verify(loaneeLoanBreakDownOutputPort, times(1)).findAllLoaneeLoanBreakDownByCohortLoaneeId(cohortLoanee.getId());
        verifyNoMoreInteractions(loaneeOutputPort, loanReferralOutputPort, loaneeLoanBreakDownOutputPort);
    }

    @Test
    void searchDisbursedLoanByOrganizationNameAndLoaneeId(){
        Page<Loan> loans = Page.empty();
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        try{
            loan.setOrganizationName("Brown");
            loan.setLoaneeId(loanee.getId());
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.searchLoanByOrganizationNameAndLoaneeId(loan))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.searchDisbursedLoan(loan);
        }catch (MeedlException e){
            log.error("Error viewing all loans: {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    void searchDisbursedLoanByOrganizationNameAndUserId(){
        Page<Loan> loans = Page.empty();
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        try{
            loan.setOrganizationName("H");
            userIdentity.setRole(IdentityRole.LOANEE);
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.searchLoanByOrganizationNameAndUserId(loan,userIdentity.getId()))
                    .thenReturn(new PageImpl<>(List.of(loan)));
            loans = loanService.searchDisbursedLoan(loan);
        }catch (MeedlException e){
            log.error("Error viewing all loans: {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    void searchDisbursedLoanByNotExistingOrganizationNameAndUserId(){
        Page<Loan> loans = Page.empty();
        loan.setPageNumber(pageNumber);
        loan.setPageSize(pageSize);
        try{
            loan.setOrganizationName("z");
            userIdentity.setRole(IdentityRole.LOANEE);
            when(userIdentityOutputPort.findById(loan.getActorId())).thenReturn(userIdentity);
            when(loanOutputPort.searchLoanByOrganizationNameAndUserId(loan,userIdentity.getId()))
                    .thenReturn(null);
            loans = loanService.searchDisbursedLoan(loan);
        }catch (MeedlException e){
            log.error("Error viewing all loans: {}", e.getMessage());
        }
        assertNull(loans);
    }


    @Test
    void cannotStartLoanForALoaneeThatTheLoanDoesNotBelongToLoanee() throws MeedlException {
        when(loaneeOutputPort.findLoaneeById(loan.getLoaneeId())).thenReturn(loanee);
        when(loanOfferOutputPort.findLoanOfferById(loan.getLoanOfferId())).thenReturn(loanOffer);
        assertThrows(MeedlException.class, () -> loanService.startLoan(loan));
    }


}
