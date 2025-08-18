package africa.nkwadoma.nkwadoma.domain.service.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.input.notification.LoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.aes.AesOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.creditregistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.AsynchronousNotificationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.UploadedStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DeferProgramRequest;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoanReferral;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class LoaneeServiceTest {
    @InjectMocks
    private LoaneeService loaneeService;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    @Mock
    private LoaneeEmailUsecase sendLoaneeEmailUsecase;
    @Mock
    private CreditRegistryOutputPort creditRegistryOutputPort;
    @Mock
    private CohortOutputPort cohortOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private  LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private LoanReferralOutputPort loanReferralOutputPort;
    @Mock
    private ProgramCohort programCohort;
    @Mock
    private ProgramOutputPort programOutputPort;
    @Mock
    private AesOutputPort aesOutputPort;
    @Mock
    private LoanMetricsUseCase loanMetricsUseCase;
    @Mock
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    @Mock
    private AesOutputPort tokenUtils;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    @Mock
    private LoanOutputPort loanOutputPort;
    @Mock
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
    @Mock
    private AsynchronousNotificationOutputPort asynchronousNotificationOutputPort;
    @Mock
    private LoanOfferOutputPort loanOfferOutputPort;
    @Mock
    private CohortLoaneeOutputPort cohortLoaneeOutputPort;

    private int pageSize = 2;
    private int pageNumber = 1;

    private Cohort elites;
    private Program atlasProgram;
    private Loanee firstLoanee;
    private final String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetails;
    private LoaneeLoanBreakdown loanBreakdown;
    private LoanReferral loanReferral;
    private OrganizationIdentity organizationIdentity;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private LoanProduct loanProduct;
    private Loan loan;
    private DeferProgramRequest deferProgramRequest;
    private String userId = "2bc2ef97-1035-5e42-bc8b-22a90b809f8d";
    private Loanee cohortLoanee;
    private CohortLoanee loaneeCohort;
    private String reasonForDeferral = "My head no carry coding again";
    private LoanOffer loanOffer;
    @Mock
    private LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;
    private LoaneeLoanAggregate loaneeLoanAggregate;
    @BeforeEach
    void setUpLoanee() {
        userIdentity = UserIdentity.builder()
                .id(mockId)
                .email("qudus55@gmail.com")
                .firstName("qudus")
                .lastName("lekan")
                .createdBy("fd099d9f-8d17-46dd-a04b-32c205e33503")
                .role(IdentityRole.LOANEE)
                .createdAt(LocalDateTime.now())
                .build();

        firstLoanee = new Loanee();
        firstLoanee.setId(mockId);
        firstLoanee.setUserIdentity(userIdentity);
        firstLoanee.setCohortId(mockId);
        firstLoanee.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);

        firstLoanee.setDeferReason("My head no carry coding again");
        firstLoanee.setLoanId(mockId);
        firstLoanee.setDeferralRequested(true);
        firstLoanee.setDeferralApproved(true);

        loanBreakdown = new LoaneeLoanBreakdown();
        loanBreakdown.setLoaneeLoanBreakdownId(mockId);
        loanBreakdown.setCurrency("usd");
        loanBreakdown.setItemAmount(BigDecimal.valueOf(100));
        loanBreakdown.setItemName("juno");

        loaneeLoanDetails = new LoaneeLoanDetail();
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(750));
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(50));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        firstLoanee.setCreatedAt(LocalDateTime.now());
        firstLoanee.setLoanBreakdowns(List.of(loanBreakdown));
        firstLoanee.setInstitutionName("Meedl");

        elites = new Cohort();
        elites.setId(mockId);
        elites.setStartDate(LocalDate.of(2024, 10, 18));
        elites.setProgramId(mockId);
        elites.setName("Elite");
        elites.setCreatedBy(mockId);
        elites.setCohortStatus(CohortStatus.CURRENT);
        elites.setTuitionAmount(BigDecimal.valueOf(700));
        elites.setTotalCohortFee(BigDecimal.valueOf(800));
        elites.setOrganizationId(mockId);

        programCohort = new ProgramCohort();
        programCohort.setCohort(elites);
        programCohort.setProgramId(mockId);
        programCohort.setId(mockId);

        loanReferral = new LoanReferral();
        loanReferral.setLoanee(firstLoanee);
        loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);

        organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setId(mockId);
        organizationEmployeeIdentity.setMeedlUser(userIdentity);
        organizationEmployeeIdentity.setOrganization(mockId);

        organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setId(organizationEmployeeIdentity.getOrganization());
        organizationIdentity.setName("Semicolon");
        organizationIdentity.setOrganizationEmployees(List.of(organizationEmployeeIdentity));
        organizationIdentity.setCreatedBy(mockId);

        atlasProgram = TestData.createProgramTestData("AtlasProgram");

        loanProduct = TestData.buildTestLoanProduct();
        loan = TestData.createTestLoan(firstLoanee);

        cohortLoanee = Loanee.builder()
                .cohortId(mockId)
                .loaneeStatus(null)
                .loanStatus(null)
                .build();

        loan = TestData.createTestLoan(firstLoanee);
        loanOffer = new LoanOffer();
        loanOffer.setId(mockId);
        loanOffer.setLoanProduct(loanProduct);

        loaneeCohort = TestData.buildCohortLoanee(firstLoanee,elites,loaneeLoanDetails,mockId);
        loaneeLoanAggregate = TestData.buildLoaneeLoanAggregate(firstLoanee);
    }

    @Test
    void addNewLoaneeToCohort() throws MeedlException {
        OrganizationEmployeeIdentity mockEmployeeIdentity = new OrganizationEmployeeIdentity();
        mockEmployeeIdentity.setId(mockId);
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        when(loaneeOutputPort.findByLoaneeEmail(userIdentity.getEmail())).thenReturn(null);
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);
        firstLoanee.setActivationStatus(ActivationStatus.ACTIVE);
        when(loaneeLoanDetailsOutputPort.save(any())).thenReturn(loaneeLoanDetails);
        when(identityManagerOutputPort.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(identityManagerOutputPort.createUser(userIdentity)).thenReturn(userIdentity);
        when(userIdentityOutputPort.save(userIdentity)).thenReturn(userIdentity);
        when(loaneeOutputPort.save(any())).thenReturn(firstLoanee);
        when(loaneeLoanAggregateOutputPort.save(any())).thenReturn(loaneeLoanAggregate);
        when(cohortLoaneeOutputPort.save(any())).thenReturn(loaneeCohort);

        when(cohortOutputPort.save(any())).thenReturn(elites);
        when(programOutputPort.findProgramById(any())).thenReturn(atlasProgram);
        when(programOutputPort.saveProgram(any())).thenReturn(atlasProgram);
        when(organizationIdentityOutputPort.findById(any())).thenReturn(organizationIdentity);
//
        when(organizationIdentityOutputPort.save(organizationIdentity))
                .thenReturn(organizationIdentity);
        Loanee loanee = loaneeService.addLoaneeToCohort(firstLoanee);
        assertEquals(firstLoanee.getUserIdentity().getFirstName(), loanee.getUserIdentity().getFirstName());
        verify(loaneeOutputPort, times(1)).save(firstLoanee);
        verify(cohortOutputPort, times(1)).save(any(Cohort.class));
    }


    @Test
    void cannotAddLoaneeWithZeroOrNegativeAmountToCohort() throws MeedlException {
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(800));
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(0));
        when(loaneeOutputPort.findByLoaneeEmail(firstLoanee.getUserIdentity().getEmail())).thenReturn(null);
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void loaneeAmountRequestedCannotBeMoreThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void loaneeInitialDepositCannotBeGreaterThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cohortTutionHaveToBeUpdatedBeforeAddingALoaneeToACohort() throws MeedlException {
        elites.setTuitionAmount(null);
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void viewAllLoaneeInCohort() throws MeedlException {
        when(cohortLoaneeOutputPort.findAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber)).
                thenReturn(new PageImpl<>(List.of(loaneeCohort)));
        Page<CohortLoanee> loanees = loaneeService.viewAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber);
        assertEquals(1,loanees.toList().size());
    }

    @Test
    void viewAllLoaneeInCohortWithNullId() {
        cohortLoanee.setCohortId(null);
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void viewAllLoaneeInCohortWithEmptyId(String cohortId) {
        cohortLoanee.setCohortId(cohortId);
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-id"})
    void viewAllLoaneeInCohortWithInvalidId(String cohortId) {
        cohortLoanee.setCohortId(cohortId);
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber));
    }
    @Test
    void referTrainee() throws MeedlException {
        try{
            loaneeCohort.setId(mockId);
            loaneeCohort.setLoaneeStatus(LoaneeStatus.ADDED);
            loaneeCohort.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);
            when(organizationIdentityOutputPort.findOrganizationByCohortId(any())).thenReturn(organizationIdentity);

            when(loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(loaneeCohort.getLoanee().getId()
                    ,loaneeCohort.getCohort().getId())).thenReturn(null);
            when(cohortOutputPort.save(elites)).thenReturn(elites);

            loanReferral.setCohortLoanee(loaneeCohort);
            loanReferral.setLoanReferralStatus(LoanReferralStatus.PENDING);

            when(loanReferralOutputPort.save(any())).thenReturn(loanReferral);

            when(cohortOutputPort.save(loaneeCohort.getCohort())).thenReturn(elites);

            LoanMetrics loanMetrics = LoanMetrics.builder().build();

            when(loanMetricsOutputPort.findByOrganizationId(anyString())).
                    thenReturn(Optional.of(loanMetrics));

            when(loanMetricsOutputPort.save(loanMetrics)).thenReturn(loanMetrics);

            when(loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByCohortLoaneeId(loaneeCohort.getId())).thenReturn(List.of(loanBreakdown));

            LoanReferral loanReferral = loaneeService.referLoanee(loaneeCohort);
            assertEquals(loanReferral.getCohortLoanee().getLoanee().getUserIdentity().getFirstName()
                    , firstLoanee.getUserIdentity().getFirstName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
            throw new MeedlException("Exception occurred while referring loanee test: "+ exception.getMessage());
        }

    }

    @Test
    void cannotReferALoaneeThatHasBeenReferredInACohortBefore(){
        try {

            loaneeCohort.setLoaneeStatus(LoaneeStatus.ADDED);
            loaneeCohort.setOnboardingMode(OnboardingMode.EMAIL_REFERRED);
            when(organizationIdentityOutputPort.findOrganizationByCohortId(any())).thenReturn(organizationIdentity);

            when(loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(loaneeCohort.getLoanee().getId()
                    ,loaneeCohort.getCohort().getId())).thenReturn(loanReferral);

        }catch (MeedlException exception){
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertThrows(MeedlException.class, () -> loaneeService.referLoanee(loaneeCohort));
    }

    @Test
    void cannotFindLoaneeWithNullLoaneeId(){
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        try {
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        } catch (MeedlException exception) {
            log.error("Error: {}", exception.getMessage());
        }
        assertThrows(MeedlException.class,()->loaneeService.viewLoaneeDetails(null, userIdentity.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7837783-jjduydsbghew87ew-ekyuhjuhdsj"})
    void cannotFindLoaneeWithInvalidUuid(String id) {
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        try{
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        } catch (MeedlException e) {
            log.error("Error: {}", e.getMessage());
        }
        assertThrows(MeedlException.class,() -> loaneeService.viewLoaneeDetails(id, userIdentity.getId()));
    }

    @Test
    void findLoanee() {
        firstLoanee.setId(mockId);
        firstLoanee.getUserIdentity().setBvn(null);
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);
        firstLoanee.setCreditScoreUpdatedAt(null);
        Loanee loanee = null;
        try{
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.of(firstLoanee));
            when(userIdentityOutputPort.findById(mockId)).thenReturn(firstLoanee.getUserIdentity());
//            when(creditRegistryOutputPort.getCreditScoreWithBvn(any())).thenReturn(10);
//            when(aesOutputPort.decryptAES(anyString(), anyString())).thenReturn("decrypted-bvn");
//            when(loaneeOutputPort.save(any(Loanee.class))).thenReturn(firstLoanee);
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
            when(programOutputPort.findProgramById(mockId)).thenReturn(atlasProgram);
            when(loanOfferOutputPort.findLoanOfferByLoaneeId(mockId)).thenReturn(loanOffer);
            when(organizationIdentityOutputPort.findById(anyString())).thenReturn(organizationIdentity);

            loanee = loaneeService.viewLoaneeDetails(null, firstLoanee.getUserIdentity().getId());
        } catch (MeedlException exception) {
            log.error("Error: {}", exception.getMessage());
        }

        assertNotNull(loanee);
        assertEquals(firstLoanee.getId(), loanee.getId());
        assertEquals(firstLoanee.getUserIdentity().getEmail(), loanee.getUserIdentity().getEmail());

        try {
            verify(loaneeOutputPort, times(1)).findByUserId(mockId);  // Correct verification
            verify(userIdentityOutputPort, times(1)).findById(mockId);
//            verify(creditRegistryOutputPort, times(1)).getCreditScoreWithBvn(any());
//            verify(tokenUtils, times(1)).decryptAES(anyString(), anyString());
//            verify(loaneeOutputPort, times(1)).save(firstLoanee);

        } catch (MeedlException exception) {
            log.info("Error: {}", exception.getMessage());
        }
    }

    @Test
    void updateLoaneeCreditScoreWhenCreditScoreUpdateIsDue() throws MeedlException {
        String bvnValue = "12345678900" ;
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusMonths(2));
        firstLoanee.getUserIdentity().setBvn(bvnValue);
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);

        Loanee loanee = null;
        try{
//            when(aesOutputPort.decryptAES("12345678900", "Error processing identity verification")).thenReturn("12345678900");
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.ofNullable(firstLoanee));
            when(userIdentityOutputPort.findById(mockId)).thenReturn(firstLoanee.getUserIdentity());
            when(creditRegistryOutputPort.getCreditScoreWithBvn(any())).thenReturn(10);
            when(loaneeOutputPort.save(any(Loanee.class))).thenReturn(firstLoanee);
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
            when(programOutputPort.findProgramById(mockId)).thenReturn(atlasProgram);
            when(loanOfferOutputPort.findLoanOfferByLoaneeId(mockId)).thenReturn(loanOffer);
            when(organizationIdentityOutputPort.findById(anyString()))
                    .thenReturn(organizationIdentity);

            loanee = loaneeService.viewLoaneeDetails(null, firstLoanee.getUserIdentity().getId());
        } catch (MeedlException exception) {
            log.info("Error: {}", exception.getMessage());
        }

        assertNotNull(loanee);
        assertEquals(firstLoanee.getId(), loanee.getId());
        verify(loaneeOutputPort, times(1)).save(firstLoanee);
//        verify(aesOutputPort, times(1)).decryptAES(bvnValue, "Error processing identity verification");
    }

    @Test
    void skipLoaneeCreditScoreUpdateWhenNotDue() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusDays(15));
        firstLoanee.getUserIdentity().setBvn("12345678910");
        when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.ofNullable(firstLoanee));
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        when(programOutputPort.findProgramById(mockId)).thenReturn(atlasProgram);
        when(loanOfferOutputPort.findLoanOfferByLoaneeId(mockId)).thenReturn(loanOffer);
        when(organizationIdentityOutputPort.findById(mockId)).thenReturn(organizationIdentity);
        Loanee result = loaneeService.viewLoaneeDetails(null, mockId);

        assertNotNull(result);
        assertEquals(firstLoanee.getId(), result.getId());
        verify(loaneeOutputPort, never()).save(firstLoanee);
    }

    @Test
    void loaneeDetailsSuccessfullyRetrievedWhenCreditScoreUpdateIsSkipped() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusDays(10));
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);

        Loanee loanee = null;
        try{
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.ofNullable(firstLoanee));
            when(loaneeOutputPort.findByUserId(mockId)).thenReturn(Optional.of(firstLoanee));
            when(userIdentityOutputPort.findById(mockId)).thenReturn(firstLoanee.getUserIdentity());
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
            when(programOutputPort.findProgramById(mockId)).thenReturn(atlasProgram);
            when(loanOfferOutputPort.findLoanOfferByLoaneeId(mockId)).thenReturn(loanOffer);

            when(organizationIdentityOutputPort.findById(mockId)).thenReturn(organizationIdentity);
            loanee = loaneeService.viewLoaneeDetails(null, firstLoanee.getUserIdentity().getId());
        } catch (MeedlException exception) {
            log.info("Error: {}", exception.getMessage());
        }

        assertNotNull(loanee);
        assertEquals(firstLoanee.getCreditScoreUpdatedAt(), loanee.getCreditScoreUpdatedAt());
        verify(loaneeOutputPort, never()).save(firstLoanee);
    }


    @Test
    void searchLoaneeInACohort(){
        Page<CohortLoanee> loanees = Page.empty();
        try {
            loaneeCohort.getLoanee().setLoaneeName("q");
            loaneeCohort.setCohort(elites);
            loaneeCohort.setUploadedStatus(UploadedStatus.ADDED);
            Page<CohortLoanee> expectedLoanees = new PageImpl<>(List.of(loaneeCohort));
            when(cohortLoaneeOutputPort.searchForLoaneeInCohort(any(Loanee.class), eq(pageSize), eq(pageNumber)))
                    .thenReturn(expectedLoanees);
            loanees = loaneeService.searchForLoaneeInCohort(loaneeCohort.getLoanee(),pageSize,pageNumber);
        }catch (MeedlException exception){
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(1,loanees.getContent().size());
    }


    @Test
    void cannotFindAllLoanBeneficiriesFromLoanProductWithNullLoanProductId(){
        assertThrows(MeedlException.class,()-> loaneeService.viewAllLoaneeThatBenefitedFromLoanProduct(null,
                pageSize,pageNumber));

    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,"jhfdsyhj-=jwuy"})
    void cannotFindAllLoanBeneficiriesFromLoanProductWithInvalidLoanProductId(String loanProductId){
        assertThrows(MeedlException.class,()-> loaneeService.viewAllLoaneeThatBenefitedFromLoanProduct(loanProductId,
                pageSize,pageNumber));

    }

    @Test
    void findALLLoanBeneficiariesFromLoanProduct(){
        Page<CohortLoanee> loanees = new  PageImpl<>(List.of(loaneeCohort));
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(cohortLoaneeOutputPort.findAllLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),pageSize,pageNumber))
                    .thenReturn(loanees);
            loanees = loaneeService.viewAllLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(loanees);
        assertEquals(1,loanees.getContent().size());
    }

    @Test
    void searchLoanBeneficiariesFromLoanProduct(){
        Page<CohortLoanee> loanees = new  PageImpl<>(List.of(loaneeCohort));
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(cohortLoaneeOutputPort.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"q",pageSize,pageNumber))
                    .thenReturn(loanees);
            loanees = loaneeService.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"q",pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(loanees);
        assertEquals(1,loanees.getContent().size());
    }


    @Test
    void searchLoanBeneficiariesFromLoanProductReturnEmptyListIfNonIsFound(){
        Page<CohortLoanee> loanees = Page.empty();
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(cohortLoaneeOutputPort.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"z",pageSize,pageNumber))
                    .thenReturn(loanees);
            loanees = loaneeService.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"z",pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertTrue(loanees.getContent().isEmpty());
        assertEquals(0,loanees.getContent().size());
    }

    @Test
    void deferProgramThrowsWhenCohortIsIncoming() {

        Loan loan = new Loan();
        loan.setLoaneeId(mockId);
        loan.setLoanStatus(LoanStatus.PERFORMING);

        Loanee foundLoanee = new Loanee();
        userIdentity.setId(userId);
        foundLoanee.setUserIdentity(userIdentity);
        foundLoanee.setCohortId(mockId);

        Cohort cohort = new Cohort();
        cohort.setCohortStatus(CohortStatus.INCOMING);

        try{
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(foundLoanee);
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(cohort);
        } catch (MeedlException e){
            log.info("Error: {}", e.getMessage());
        }
;
        Exception message = assertThrows(MeedlException.class, () ->
                loaneeService.deferLoan(userId, mockId, reasonForDeferral));
    }

    @Test
    void deferProgramThrowsWhenCohortIsGraduated() {
        Loanee loanee = new Loanee();
        loanee.setLoanId(mockId);
        loanee.setId(mockId);
        loanee.setDeferReason("Hunger want finish me");

        Loan loan = new Loan();
        loan.setLoaneeId(mockId);
        loan.setLoanStatus(LoanStatus.PERFORMING);

        Loanee foundLoanee = new Loanee();
        userIdentity.setId(userId);
        foundLoanee.setUserIdentity(userIdentity);
        foundLoanee.setCohortId(mockId);

        Cohort cohort = new Cohort();
        cohort.setCohortStatus(CohortStatus.GRADUATED);

        try{
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(foundLoanee);
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(cohort);
        } catch (MeedlException e){
            log.info("Error: {}", e.getMessage());
        }

        Exception message = assertThrows(MeedlException.class, () ->
                loaneeService.deferLoan(userId, mockId, reasonForDeferral));
    }

    @Test
    void deferProgramWithAnotherUser() {
        Loan loan = new Loan();
        loan.setLoaneeId(mockId);

        Loanee foundLoanee = new Loanee();
        foundLoanee.setUserIdentity(userIdentity);
        foundLoanee.getUserIdentity().setId("different-user-id");

        try {
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(foundLoanee);
        } catch (MeedlException e) {
            log.info("Error: {}", e.getMessage());
        }

        Exception e = assertThrows(MeedlException.class, () ->
                loaneeService.deferLoan(userId, mockId, reasonForDeferral));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-uuid"})
    void deferProgramThrowsWhenLoanIdInvalid(String loanId) {
        Loanee loanee = new Loanee();
        loanee.setLoanId(loanId);

        assertThrows(MeedlException.class, () ->
                loaneeService.deferLoan(userId, loanId, reasonForDeferral));
    }

    @Test
    void deferProgramSuccessfully() {
        Loan loan = new Loan();
        loan.setId(mockId);
        loan.setLoaneeId(mockId);
        loan.setLoanStatus(LoanStatus.PERFORMING);
        loan.setLoanee(firstLoanee);

        elites.setStartDate(LocalDate.now());

        String result = null;
        try{
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
            when(cohortOutputPort.findCohortById(anyString())).thenReturn(elites);
            when(loanOutputPort.save(any(Loan.class))).thenReturn(loan);
            when(programOutputPort.findProgramById(mockId)).thenReturn(atlasProgram);
            result = loaneeService.deferLoan(mockId, mockId, reasonForDeferral);
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }

        assertEquals("Deferral request sent", result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-uuid"})
    void resumeProgramThrowsWhenLoanIdIsInvalid(String loanId) {
        Loanee loanee = new Loanee();
        loanee.setLoanId(loanId);

        assertThrows(MeedlException.class, () ->
                loaneeService.resumeProgram(loanId, mockId, mockId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "invalid-uuid"})
    void resumeProgramThrowsWhenCohortIdIsInvalid(String cohortId) {
        assertThrows(MeedlException.class, () ->
                loaneeService.resumeProgram(mockId, cohortId, mockId));
    }

    @Test
    void resumeProgramSuccessfully() {
        Loan loan = new Loan();
        loan.setLoaneeId(mockId);
        loan.setLoanStatus(LoanStatus.DEFERRED);

        Loanee foundLoanee = new Loanee();
        userIdentity.setId(userId);
        foundLoanee.setUserIdentity(userIdentity);
        foundLoanee.setCohortId(mockId);

        Cohort cohort = new Cohort();
        cohort.setId(mockId);
        cohort.setCohortStatus(CohortStatus.CURRENT);
        String result = null;
        try{
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(foundLoanee);
            when(cohortOutputPort.findCohortById(anyString())).thenReturn(cohort);
            when(loanOutputPort.save(any(Loan.class))).thenReturn(loan);
            result = loaneeService.resumeProgram(mockId, cohort.getId(), userId);
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }

        assertEquals("Successfully resumed", result);
    }

    @Test
    void resumeToAGraduatedCohort() {
        Loan loan = new Loan();
        loan.setLoaneeId(mockId);
        loan.setLoanStatus(LoanStatus.DEFERRED);

        Loanee foundLoanee = new Loanee();
        userIdentity.setId(userId);
        foundLoanee.setUserIdentity(userIdentity);
        foundLoanee.setCohortId(mockId);

        Cohort cohort = new Cohort();
        cohort.setId(mockId);
        cohort.setCohortStatus(CohortStatus.GRADUATED);
        try{
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(foundLoanee);
            when(cohortOutputPort.findCohortById(anyString())).thenReturn(cohort);
        } catch (Exception e) {
            log.info("Error: {}", e.getMessage());
        }

        assertThrows(MeedlException.class, () -> loaneeService.
                resumeProgram(mockId, cohort.getId(), userId));
    }

    @Test
    void indicateDeferredLoanee() throws MeedlException {
        String result = "";
        loan.setLoanStatus(LoanStatus.PERFORMING);
        elites.setStartDate(LocalDate.now());
        log.info("-----------> loanee -----------> {}",firstLoanee);
        try{
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId()))
                .thenReturn(Optional.of(organizationEmployeeIdentity));
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(firstLoanee.getId(), organizationEmployeeIdentity.getOrganization()))
                .thenReturn(true);

        when(loanOutputPort.findLoanByLoanOfferId(firstLoanee.getId())).thenReturn(Optional.of(loan));
        when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
        when(programOutputPort.findProgramById(mockId)).thenReturn(atlasProgram);
        when(userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER))
                .thenReturn(Collections.singletonList(userIdentity));
         result = loaneeService.indicateDeferredLoanee(mockId, firstLoanee.getId());
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        verify(loanOutputPort).save(argThat(l -> l.getLoanStatus() == LoanStatus.DEFERRED));
        verify(meedlNotificationOutputPort, times(2)).save(any(MeedlNotification.class));
        assertEquals("Loanee has been Deferred", result);
    }

    @Test
    void shouldThrowExceptionWhenLoaneeNotAssociatedWithOrganization() throws MeedlException {
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId()))
                .thenReturn(Optional.of(organizationEmployeeIdentity));
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(firstLoanee.getId(), organizationEmployeeIdentity.getOrganization()))
                .thenReturn(false);
        assertThrows(MeedlException.class,()-> loaneeService.indicateDeferredLoanee(mockId, mockId));
    }

    @Test
    void indicateDropOutLoanee() throws MeedlException {
        String loanId = mockId;
        Loan loan = new Loan();
        loan.setLoaneeId(loanId);
        firstLoanee.setId(loanId);
        firstLoanee.setDropoutRequested(true);

        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId()))
                .thenReturn(Optional.of(organizationEmployeeIdentity));
        when(loaneeOutputPort.findLoaneeById(loanId)).thenReturn(firstLoanee);
        when(loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(loanId, organizationEmployeeIdentity.getOrganization()))
                .thenReturn(true);
        when(userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER))
                .thenReturn(Collections.singletonList(userIdentity));
        when(loanOutputPort.findLoanById(loanId)).thenReturn(loan);
        when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
        when(loanOutputPort.save(loan)).thenReturn(loan);

        String response = loaneeService.indicateDropOutLoanee(mockId, loanId);
        assertEquals("Loanee has been dropped out", response);
        verify(meedlNotificationOutputPort, times(2)).save(any(MeedlNotification.class));
        verify(loanOutputPort).save(argThat(l ->
                l.getLoanStatus() == LoanStatus.DROPOUT));
    }

    @Test
    void dropOutFromCohortByLoanee() throws MeedlException {
        String response = "";
        try {
            String reason = "School na scam";
            when(loaneeOutputPort.findLoaneeById(any())).thenReturn(firstLoanee);
            elites.setStartDate(LocalDate.now());
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
            firstLoanee.setCohortId(elites.getId());
            elites.setProgramId(atlasProgram.getId());
            atlasProgram.setDuration(4);
            when(programOutputPort.findProgramById(elites.getProgramId())).thenReturn(atlasProgram);
            when(organizationEmployeeIdentityOutputPort
                    .findAllEmployeesInOrganizationByOrganizationIdAndRole(
                            atlasProgram.getOrganizationId(), IdentityRole.ORGANIZATION_ADMIN))
                    .thenReturn(List.of(organizationEmployeeIdentity));
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            when(userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER))
                    .thenReturn(List.of(userIdentity));
            response = loaneeService.dropOutFromCohort(mockId, mockId, reason);
        } catch (MeedlException meedlException) {
            log.error(meedlException.getMessage());
            fail("Unexpected MeedlException: " + meedlException.getMessage());
        }

        verify(meedlNotificationOutputPort, times(2)).save(any(MeedlNotification.class));
        assertEquals("Dropout request sent", response);
    }

    @Test
    void indicateDropOutLoanee_WhenDropoutIsNotRequested_ShouldNotChangeLoanStatus() throws MeedlException {
        String loanId = mockId;
        Loan loan = new Loan();
        loan.setLoaneeId(loanId);
        loan.setLoanStatus(LoanStatus.PERFORMING);

        firstLoanee.setId(loanId);
        firstLoanee.setDropoutRequested(false);
        firstLoanee.setDropoutApproved(false);

        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId()))
                .thenReturn(Optional.of(organizationEmployeeIdentity));
        when(loaneeOutputPort.findLoaneeById(loanId)).thenReturn(firstLoanee);
        when(loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(loanId, organizationEmployeeIdentity.getOrganization()))
                .thenReturn(true);
        when(loanOutputPort.findLoanById(loanId)).thenReturn(loan);
        when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
        assertFalse(firstLoanee.isDropoutApproved());
        String response = loaneeService.indicateDropOutLoanee(mockId, loanId);
        assertEquals("Loanee has been dropped out", response);
        assertTrue(firstLoanee.isDropoutApproved());
        verify(loanOutputPort, never()).save(any());
        verify(meedlNotificationOutputPort, times(1)).save(any(MeedlNotification.class));
    }

    @Test
    void loanneCannotDropOutFromCohortThatHasGonePastFirstQuarter() throws MeedlException {
        String reason = "School na scam";
        try {
            when(loaneeOutputPort.findLoaneeById(any())).thenReturn(firstLoanee);
            elites.setStartDate(LocalDate.now().minusMonths(6));
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(elites);
            when(loanOutputPort.findLoanById(mockId)).thenReturn(loan);
            firstLoanee.setCohortId(elites.getId());
            elites.setProgramId(atlasProgram.getId());
            atlasProgram.setDuration(12);
            when(programOutputPort.findProgramById(elites.getProgramId())).thenReturn(atlasProgram);
        }catch (MeedlException meedlException) {
            log.error(meedlException.getMessage());
        }
            assertThrows(MeedlException.class,()-> loaneeService.dropOutFromCohort(mockId,mockId,reason));
    }

    @Test
    void archiveLoanee(){

        String response = "";
        try{
            response = loaneeService.archiveOrUnArchiveByIds(elites.getId(),List.of(firstLoanee.getId()),LoaneeStatus.ARCHIVE);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
       assertEquals(response,"Loanee has been "+LoaneeStatus.ARCHIVE.name());
    }

    @Test
    void viewAllLoaneeInCohortWithUploadedStatusAdded() throws MeedlException {
        loaneeCohort.setUploadedStatus(UploadedStatus.ADDED);
        Page<CohortLoanee> expectedPage = new PageImpl<>(List.of(loaneeCohort));
        when(cohortLoaneeOutputPort.findAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber)).
                thenReturn(expectedPage);

        Page<CohortLoanee> result = loaneeService.viewAllLoaneeInCohort(cohortLoanee, pageSize, pageNumber);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(loaneeCohort, result.getContent().get(0));
        assertEquals(UploadedStatus.ADDED, loaneeCohort.getUploadedStatus());
        verify(cohortLoaneeOutputPort, times(1)).findAllLoaneeInCohort(cohortLoanee, pageSize, pageNumber);
    }
    @Test
    void viewLoaneeLoanDetail_withValidId_returnsLoanDetail() throws MeedlException {
        when(loaneeLoanDetailsOutputPort.findByCohortLoaneeId(mockId))
                .thenReturn(loaneeLoanDetails);

        LoaneeLoanDetail result = loaneeService.viewLoaneeLoanDetail(mockId);

        assertNotNull(result);
        assertEquals(loaneeLoanDetails.getAmountRequested(), result.getAmountRequested());
        verify(loaneeLoanDetailsOutputPort, times(1)).findByCohortLoaneeId(mockId);
    }
    @Test
    void viewLoaneeLoanDetail_withInvalidUUID_throwsMeedlException() {
        String invalidId = "not-a-uuid";

        MeedlException exception = assertThrows(MeedlException.class,
                () -> loaneeService.viewLoaneeLoanDetail(invalidId));

        assertEquals("Provide valid loanee loan detail id", exception.getMessage());
    }


    @Test
    void viewAllLoaneeInCohortWithUploadedInvited() throws MeedlException {
        loaneeCohort.setUploadedStatus(UploadedStatus.INVITED);
        Page<CohortLoanee> expectedPage = new PageImpl<>(List.of(loaneeCohort));
        when(cohortLoaneeOutputPort.findAllLoaneeInCohort(cohortLoanee,pageSize,pageNumber)).
                thenReturn(expectedPage);

        Page<CohortLoanee> result = loaneeService.viewAllLoaneeInCohort(cohortLoanee, pageSize, pageNumber);
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(loaneeCohort, result.getContent().get(0));
        assertEquals(UploadedStatus.INVITED, loaneeCohort.getUploadedStatus());
        verify(cohortLoaneeOutputPort, times(1)).findAllLoaneeInCohort(cohortLoanee, pageSize, pageNumber);
    }

    @Test
    void viewLoaneeInCohort() throws MeedlException {
        when(cohortLoaneeOutputPort.findCohortLoaneeByLoaneeIdAndCohortId(mockId,mockId)).thenReturn(loaneeCohort);
        CohortLoanee result = loaneeService.viewLoaneeDetailInCohort(mockId,mockId);
        assertNotNull(result);
    }


    @Test
    void searchLoanAggregateByPm(){
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        Page<LoaneeLoanAggregate> loanAggregatePage = new PageImpl<>(List.of(loaneeLoanAggregate));
        firstLoanee.setLoaneeName("q");
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(userIdentity);
            when(loaneeLoanAggregateOutputPort.searchLoanAggregate(firstLoanee.getLoaneeName(),pageSize,pageNumber))
                    .thenReturn(loanAggregatePage);
            loanAggregatePage = loaneeService.searchLoanAggregate(firstLoanee,pageSize,pageNumber);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNotNull(loanAggregatePage);
    }

    @Test
    void searchLoanAggregateByOrganizationAdmin(){
        userIdentity.setRole(IdentityRole.ORGANIZATION_ADMIN);
        firstLoanee.setOrganizationId(organizationEmployeeIdentity.getOrganization());
        firstLoanee.setLoaneeName("q");
        Page<LoaneeLoanAggregate> loanAggregatePage = new PageImpl<>(List.of(loaneeLoanAggregate));
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(userIdentity);
            when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId())).thenReturn(Optional.ofNullable(organizationEmployeeIdentity));
            when(loaneeLoanAggregateOutputPort.searchLoanAggregateByOrganizationId(firstLoanee,pageSize,pageNumber))
                    .thenReturn(loanAggregatePage);
            loanAggregatePage = loaneeService.searchLoanAggregate(firstLoanee,pageSize,pageNumber);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNotNull(loanAggregatePage);
    }

    @Test
    void viewAllLoanAggregateByPm(){
        userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
        Page<LoaneeLoanAggregate> loanAggregatePage = new PageImpl<>(List.of(loaneeLoanAggregate));
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(userIdentity);
            when(loaneeLoanAggregateOutputPort.findAllLoanAggregate(pageSize,pageNumber))
                    .thenReturn(loanAggregatePage);
            loanAggregatePage = loaneeService.viewAllLoanee(mockId,pageSize,pageNumber);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNotNull(loanAggregatePage);
    }

    @Test
    void viewAllLoanAggregateByOrganizationAdmin(){
        userIdentity.setRole(IdentityRole.ORGANIZATION_ADMIN);
        firstLoanee.setOrganizationId(organizationEmployeeIdentity.getOrganization());
        Page<LoaneeLoanAggregate> loanAggregatePage = new PageImpl<>(List.of(loaneeLoanAggregate));
        try {
            when(userIdentityOutputPort.findById(any())).thenReturn(userIdentity);
            when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId())).thenReturn(Optional.ofNullable(organizationEmployeeIdentity));
            when(loaneeLoanAggregateOutputPort.findAllLoanAggregateByOrganizationId(organizationEmployeeIdentity.getOrganization(),pageSize,pageNumber))
                    .thenReturn(loanAggregatePage);
            loanAggregatePage = loaneeService.viewAllLoanee(mockId,pageSize,pageNumber);
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertNotNull(loanAggregatePage);
    }

}
