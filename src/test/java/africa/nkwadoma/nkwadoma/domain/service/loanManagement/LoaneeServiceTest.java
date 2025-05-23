package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.email.LoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.loanManagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.OnboardingMode;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.infrastructure.utilities.*;
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
    private LoanMetricsUseCase loanMetricsUseCase;
    @Mock
    private LoaneeLoanBreakDownOutputPort loaneeLoanBreakDownOutputPort;
    @Mock
    private TokenUtils tokenUtils;
    @Mock
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Mock
    private LoanProductOutputPort loanProductOutputPort;
    @Mock
    private LoanOutputPort loanOutputPort;
    @Mock
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
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

        elites = new Cohort();
        elites.setId(mockId);
        elites.setStartDate(LocalDate.of(2024, 10, 18));
        elites.setProgramId(mockId);
        elites.setName("Elite");
        elites.setCreatedBy(mockId);
        elites.setTuitionAmount(BigDecimal.valueOf(700));
        elites.setTotalCohortFee(BigDecimal.valueOf(800));

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

    }


    @Test
    void addLoaneeToCohort() throws MeedlException {
        OrganizationEmployeeIdentity mockEmployeeIdentity = new OrganizationEmployeeIdentity();
        mockEmployeeIdentity.setId(mockId);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        when(loaneeLoanDetailsOutputPort.save(any())).thenReturn(loaneeLoanDetails);
        when(identityManagerOutputPort.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(identityManagerOutputPort.createUser(userIdentity)).thenReturn(userIdentity);
        when(userIdentityOutputPort.save(userIdentity)).thenReturn(userIdentity);
        when(loaneeOutputPort.save(any())).thenReturn(firstLoanee);
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
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void loaneeAmountRequestedCannotBeMoreThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setAmountRequested(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void loaneeInitialDepositCannotBeGreaterThanCohortTotalFee() throws MeedlException {
        loaneeLoanDetails.setInitialDeposit(BigDecimal.valueOf(7000));
        elites.setTotalCohortFee(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetails);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cohortTutionHaveToBeUpdatedBeforeAddingALoaneeToACohort() throws MeedlException {
        elites.setTuitionAmount(null);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }


    @Test
    void cannotAddLoaneeToACohortWithExistingLoaneeEmail() throws MeedlException {
        when(loaneeOutputPort.findByLoaneeEmail(userIdentity.getEmail())).thenReturn(firstLoanee);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void viewAllLoaneeInCohort() throws MeedlException {
        when(loaneeOutputPort.findAllLoaneeByCohortId(mockId,pageSize,pageNumber, MeedlMessages.CREATED_AT.getMessage())).
                thenReturn(new PageImpl<>(List.of(firstLoanee)));
        Page<Loanee> loanees = loaneeService.viewAllLoaneeInCohort(mockId,pageSize,pageNumber, MeedlMessages.CREATED_AT.getMessage());
        assertEquals(1,loanees.toList().size());
    }

    @Test
    void viewAllLoaneeInCohortWithNullId() {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(null,pageSize,pageNumber, MeedlMessages.CREATED_AT.getMessage()));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void viewAllLoaneeInCohortWithEmptyId(String cohortId) {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortId,pageSize,pageNumber, MeedlMessages.CREATED_AT.getMessage()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-id"})
    void viewAllLoaneeInCohortWithInvalidId(String cohortId) {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortId,pageSize,pageNumber, MeedlMessages.CREATED_AT.getMessage()));
    }
    @Test
    void referTrainee() throws MeedlException {
        try{
            firstLoanee.setLoaneeStatus(LoaneeStatus.ADDED);
            when(loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(firstLoanee.getId(),firstLoanee.getCohortId()))
                    .thenReturn(null);
            when(cohortOutputPort.findCohort(firstLoanee.getCohortId())).thenReturn(elites);
            when(loaneeLoanBreakDownOutputPort.findAllLoaneeLoanBreakDownByLoaneeId(firstLoanee.getId())).thenReturn(List.of(loanBreakdown));
            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(any()))
                    .thenReturn(organizationEmployeeIdentity);
            when(organizationIdentityOutputPort.findById(mockId)).
                    thenReturn(organizationIdentity);
            when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
            when(cohortOutputPort.save(elites)).thenReturn(elites);
//            doNothing().when(sendLoaneeEmailUsecase).sendLoaneeHasBeenReferEmail(any());
            when(loanReferralOutputPort.createLoanReferral(firstLoanee)).thenReturn(loanReferral);
            when(loanMetricsOutputPort.findByOrganizationId(organizationEmployeeIdentity.getOrganization()))
                    .thenReturn(Optional.of(new LoanMetrics()));
            when(loanMetricsOutputPort.save(any())).thenReturn(new LoanMetrics());
            LoanReferral loanReferral = loaneeService.referLoanee(firstLoanee);
            assertEquals(loanReferral.getLoanee().getUserIdentity().getFirstName()
                    , firstLoanee.getUserIdentity().getFirstName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
            throw new MeedlException("Exception occurred while referring loanee test: "+ exception.getMessage());
        }

    }

    @Test
    void cannotReferALoaneeThatHasBeenReferredInACohortBefore(){
        try {
            firstLoanee.setLoaneeStatus(LoaneeStatus.ADDED);
            when(loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(firstLoanee.getId(), firstLoanee.getCohortId()))
                    .thenReturn(new LoanReferral());
            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(anyString()))
                    .thenReturn(organizationEmployeeIdentity);
            when(organizationIdentityOutputPort.findById(anyString()))
                    .thenReturn(organizationIdentity);
        }catch (MeedlException exception){
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertThrows(MeedlException.class, () -> loaneeService.referLoanee(firstLoanee));
    }

    @Test
    void cannotFindLoaneeWithNullLoaneeId(){
        assertThrows(MeedlException.class,()->loaneeService.viewLoaneeDetails(null, userIdentity.getId()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7837783-jjduydsbghew87ew-ekyuhjuhdsj"})
    void cannotFindLoaneeWithInvalidUuid(String id){
        assertThrows(MeedlException.class,() -> loaneeService.viewLoaneeDetails(id, userIdentity.getId()));
    }

    @Test
    void findLoanee() throws MeedlException {
        firstLoanee.setId(mockId);
        firstLoanee.getUserIdentity().setBvn("12345678901");
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);

        firstLoanee.setId(mockId);
        firstLoanee.setUserIdentity(userIdentity);
        firstLoanee.setCreditScoreUpdatedAt(null);

        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(creditRegistryOutputPort.getCreditScoreWithBvn(any())).thenReturn(10);
        when(tokenUtils.decryptAES(anyString(), anyString())).thenReturn("decrypted-bvn");
        Loanee result = loaneeService.viewLoaneeDetails(mockId, firstLoanee.getUserIdentity().getId());

        assertNotNull(result);
        assertEquals(firstLoanee.getId(), result.getId());
        assertEquals(firstLoanee.getUserIdentity().getEmail(), result.getUserIdentity().getEmail());

        verify(loaneeOutputPort).findLoaneeById(mockId);
        verify(userIdentityOutputPort).findById(mockId);
        verify(creditRegistryOutputPort).getCreditScoreWithBvn(any());
        verify(tokenUtils).decryptAES(anyString(), anyString());
        verify(loaneeOutputPort).save(firstLoanee);
    }

    @Test
    void updateLoaneeCreditScoreWhenCreditScoreUpdateIsDue() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusMonths(2));
        firstLoanee.getUserIdentity().setBvn("12345678900");
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);

        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(firstLoanee.getUserIdentity());
        when(creditRegistryOutputPort.getCreditScoreWithBvn(any())).thenReturn(10);
        when(tokenUtils.decryptAES(eq("12345678900"), eq("Error processing identity verification")))
                .thenReturn("decrypted-bvn");
//        when(tokenUtils.decryptAES(anyString(), any())).thenReturn(anyString());
        when(loaneeOutputPort.save(any(Loanee.class))).thenReturn(firstLoanee);

        Loanee result = loaneeService.viewLoaneeDetails(mockId, firstLoanee.getUserIdentity().getId());

        assertNotNull(result);
        assertEquals(firstLoanee.getId(), result.getId());
        verify(loaneeOutputPort, times(1)).save(firstLoanee);
    }

    @Test
    void skipLoaneeCreditScoreUpdateWhenNotDue() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusDays(15));
        firstLoanee.getUserIdentity().setBvn("12345678910");
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(userIdentityOutputPort.save(userIdentity)).thenReturn(userIdentity);
        log.info("----> user identity ---> {}", userIdentity);
        Loanee result = loaneeService.viewLoaneeDetails(mockId, mockId);

        assertNotNull(result);
        assertEquals(firstLoanee.getId(), result.getId());
        verify(loaneeOutputPort, never()).save(firstLoanee);
    }

    @Test
    void loaneeDetailsSuccessfullyRetrievedWhenCreditScoreUpdateIsSkipped() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusDays(10));
        firstLoanee.getUserIdentity().setRole(IdentityRole.LOANEE);

        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(userIdentityOutputPort.findById(mockId)).thenReturn(firstLoanee.getUserIdentity());
        Loanee result = loaneeService.viewLoaneeDetails(mockId, firstLoanee.getUserIdentity().getId());

        assertNotNull(result);
        assertEquals(firstLoanee.getCreditScoreUpdatedAt(), result.getCreditScoreUpdatedAt());
        verify(loaneeOutputPort, never()).save(firstLoanee);
    }


    @Test
    void searchLoaneeInACohort(){
        List<Loanee> loanees = new ArrayList<>();
        try {
            when(loaneeOutputPort.searchForLoaneeInCohort("le", mockId)).thenReturn(List.of(firstLoanee));
            loanees = loaneeService.searchForLoaneeInCohort("le", mockId);
        }catch (MeedlException exception){
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertEquals(1,loanees.size());
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
        Page<Loanee> loanees = new  PageImpl<>(List.of(firstLoanee));
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(loaneeOutputPort.findAllLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),pageSize,pageNumber))
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
        Page<Loanee> loanees = new  PageImpl<>(List.of(firstLoanee));
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(loaneeOutputPort.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"q",pageSize,pageNumber))
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
        Page<Loanee> loanees =  Page.empty();
        try {
            when(loanProductOutputPort.findById(loanProduct.getId())).thenReturn(loanProduct);
            when(loaneeOutputPort.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"z",pageSize,pageNumber))
                    .thenReturn(loanees);
            loanees = loaneeService.searchLoaneeThatBenefitedFromLoanProduct(loanProduct.getId(),"z",pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertTrue(loanees.getContent().isEmpty());
        assertEquals(0,loanees.getContent().size());
    }

    @Test
    void indicateDeferredLoanee() throws MeedlException {
        String result = "";
        try{
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId()))
                .thenReturn(Optional.of(organizationEmployeeIdentity));
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(firstLoanee.getId(), organizationEmployeeIdentity.getOrganization()))
                .thenReturn(true);
        when(loanOutputPort.viewLoanByLoaneeId(firstLoanee.getId())).thenReturn(Optional.of(loan));
        when(userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER))
                .thenReturn(Collections.singletonList(userIdentity));
         result = loaneeService.indicateDeferredLoanee(mockId, mockId);
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
        String response = "";
        try{
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            when(organizationEmployeeIdentityOutputPort.findByMeedlUserId(userIdentity.getId()))
                    .thenReturn(Optional.of(organizationEmployeeIdentity));
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
            when(loaneeOutputPort.checkIfLoaneeCohortExistInOrganization(firstLoanee.getId(), organizationEmployeeIdentity.getOrganization()))
                    .thenReturn(true);
            firstLoanee.setLoaneeStatus(LoaneeStatus.DROPOUT);
            when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
            when(userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER))
                    .thenReturn(Collections.singletonList(userIdentity));
            response = loaneeService.indicateDropOutLoanee(mockId,mockId);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        verify(meedlNotificationOutputPort, times(2)).save(any(MeedlNotification.class));
        assertEquals("Loanee has been dropped out", response);
    }
}







