package africa.nkwadoma.nkwadoma.domain.service.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.email.SendLoaneeEmailUsecase;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.creditRegistry.CreditRegistryOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanReferralOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanReferralStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.test.data.TestData;
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
    private SendLoaneeEmailUsecase sendLoaneeEmailUsecase;
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
    private int pageSize = 2;
    private int pageNumber = 1;

    private Cohort elites;
    private Program atlasProgram;
    private Loanee firstLoanee;
    private final String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private UserIdentity loaneeUserIdentity;
    private LoaneeLoanDetail loaneeLoanDetails;
    private LoaneeLoanBreakdown loanBreakdown;
    private LoanReferral loanReferral;
    private OrganizationIdentity organizationIdentity;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;


    @BeforeEach
    void setUpLoanee() {
        loaneeUserIdentity = UserIdentity.builder()
                .email("qudus55@gmail.com")
                .firstName("qudus")
                .lastName("lekan")
                .createdBy("fd099d9f-8d17-46dd-a04b-32c205e33503")
                .role(IdentityRole.LOANEE)
                .createdAt(LocalDateTime.now().toString())
                .build();

        firstLoanee = new Loanee();
        firstLoanee.setId(mockId);
        firstLoanee.setUserIdentity(loaneeUserIdentity);
        firstLoanee.setCohortId(mockId);

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
        organizationEmployeeIdentity.setMeedlUser(loaneeUserIdentity);
        organizationEmployeeIdentity.setOrganization(mockId);

        organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setId(organizationEmployeeIdentity.getOrganization());
        organizationIdentity.setName("Semicolon");
        organizationIdentity.setOrganizationEmployees(List.of(organizationEmployeeIdentity));
        organizationIdentity.setCreatedBy(mockId);

        atlasProgram = TestData.createProgramTestData("AtlasProgram");

    }


    @Test
    void addLoaneeToCohort() throws MeedlException {
        OrganizationEmployeeIdentity mockEmployeeIdentity = new OrganizationEmployeeIdentity();
        mockEmployeeIdentity.setId(mockId);
        when(cohortOutputPort.findCohort(mockId)).thenReturn(elites);
        when(loaneeLoanDetailsOutputPort.save(any())).thenReturn(loaneeLoanDetails);
        when(identityManagerOutputPort.getUserByEmail(anyString())).thenReturn(Optional.empty());
        when(identityManagerOutputPort.createUser(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
        when(userIdentityOutputPort.save(loaneeUserIdentity)).thenReturn(loaneeUserIdentity);
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
        when(loaneeOutputPort.findByLoaneeEmail(loaneeUserIdentity.getEmail())).thenReturn(firstLoanee);
        assertThrows(MeedlException.class, () -> loaneeService.addLoaneeToCohort(firstLoanee));
    }

    @Test
    void viewAllLoaneeInCohort() throws MeedlException {
        when(loaneeOutputPort.findAllLoaneeByCohortId(mockId,pageSize,pageNumber)).
                thenReturn(new PageImpl<>(List.of(firstLoanee)));
        Page<Loanee> loanees = loaneeService.viewAllLoaneeInCohort(mockId,pageSize,pageNumber);
        assertEquals(1,loanees.toList().size());
    }

    @Test
    void viewAllLoaneeInCohortWithNullId() {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(null,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void viewAllLoaneeInCohortWithEmptyId(String cohortId) {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortId,pageSize,pageNumber));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-id"})
    void viewAllLoaneeInCohortWithInvalidId(String cohortId) {
        assertThrows(MeedlException.class, ()-> loaneeService.viewAllLoaneeInCohort(cohortId,pageSize,pageNumber));
    }
    @Test
    void referTrainee(){
        try{
            firstLoanee.setLoaneeStatus(LoaneeStatus.ADDED);
            when(loaneeOutputPort.findLoaneeById(firstLoanee.getId())).thenReturn(firstLoanee);
            when(loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(firstLoanee.getId(),firstLoanee.getCohortId()))
                    .thenReturn(null);
            when(cohortOutputPort.findCohort(firstLoanee.getCohortId())).thenReturn(elites);
            when(loaneeLoanBreakDownOutputPort.findAllByLoaneeId(firstLoanee.getId())).thenReturn(List.of(loanBreakdown));
            when(loaneeOutputPort.findAllLoaneesByCohortId(elites.getId())).thenReturn(List.of(firstLoanee));
            when(organizationEmployeeIdentityOutputPort.findByEmployeeId(any()))
                    .thenReturn(organizationEmployeeIdentity);
            when(organizationIdentityOutputPort.findById(mockId)).
                    thenReturn(organizationIdentity);
            when(userIdentityOutputPort.findAllByRole(IdentityRole.PORTFOLIO_MANAGER)).thenReturn(List.of(loaneeUserIdentity));
            when(loaneeOutputPort.save(firstLoanee)).thenReturn(firstLoanee);
            when(cohortOutputPort.save(elites)).thenReturn(elites);
            doNothing().when(sendLoaneeEmailUsecase).sendLoaneeHasBeenReferEmail(any());
            when(loanReferralOutputPort.createLoanReferral(firstLoanee)).thenReturn(loanReferral);
            when(loanMetricsUseCase.save(any())).thenReturn(LoanMetrics.builder().
                    organizationId(organizationEmployeeIdentity.getOrganization()).
                    loanReferralCount(1).build());
            LoanReferral loanReferral = loaneeService.referLoanee(firstLoanee.getId());
            assertEquals(loanReferral.getLoanee().getUserIdentity().getFirstName()
                    , firstLoanee.getUserIdentity().getFirstName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }

    }

    @Test
    void cannotReferALoaneeThatHasBeenReferredInACohortBefore(){
        try {
            firstLoanee.setLoaneeStatus(LoaneeStatus.ADDED);
            when(loaneeOutputPort.findLoaneeById(firstLoanee.getId())).thenReturn(firstLoanee);
            when(loanReferralOutputPort.findLoanReferralByLoaneeIdAndCohortId(firstLoanee.getId(), firstLoanee.getCohortId()))
                    .thenReturn(new LoanReferral());
        }catch (MeedlException exception){
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertThrows(MeedlException.class, () -> loaneeService.referLoanee(firstLoanee.getId()));
    }

    @Test
    void cannotFindLoaneeWithNullLoaneeId(){
        assertThrows(MeedlException.class,()->loaneeService.viewLoaneeDetails(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"7837783-jjduydsbghew87ew-ekyuhjuhdsj"})
    void cannotFindLoaneeWithInvalidUuid(String id){
        assertThrows(MeedlException.class,() -> loaneeService.viewLoaneeDetails(id));
    }

    @Test
    void findLoanee(){
        Loanee loanee = new Loanee();
        try {
            firstLoanee.setId(mockId);
            firstLoanee.getUserIdentity().setBvn("12345678901");
            when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
            when(creditRegistryOutputPort.getCreditScoreWithBvn(any())).thenReturn(10);
            when(loaneeOutputPort.save(any(Loanee.class))).thenReturn(firstLoanee);
            loanee = loaneeService.viewLoaneeDetails(mockId);
            verify(loaneeOutputPort, times(1)).findLoaneeById(mockId);
        } catch (MeedlException exception) {
            log.error("Error occured",  exception);
        }
        assertEquals(firstLoanee.getId(), loanee.getId());
        assertEquals(firstLoanee.getUserIdentity().getEmail(), loanee.getUserIdentity().getEmail());
    }

    @Test
    void updateLoaneeCreditScoreWhenCreditScoreUpdateIsDue() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusMonths(2));
        firstLoanee.getUserIdentity().setBvn("12345678900");
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);
        when(creditRegistryOutputPort.getCreditScoreWithBvn(any())).thenReturn(10);
        when(loaneeOutputPort.save(any(Loanee.class))).thenReturn(firstLoanee);

        Loanee result = loaneeService.viewLoaneeDetails(mockId);

        assertNotNull(result);
        assertEquals(firstLoanee.getId(), result.getId());
        verify(loaneeOutputPort, times(1)).save(firstLoanee);
    }

    @Test
    void skipLoaneeCreditScoreUpdateWhenNotDue() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusDays(15));
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);

        Loanee result = loaneeService.viewLoaneeDetails(mockId);

        assertNotNull(result);
        assertEquals(firstLoanee.getId(), result.getId());
        verify(loaneeOutputPort, never()).save(firstLoanee);
    }
    @Test
    void loaneeDetailsSuccessfullyRetrievedWhenCreditScoreUpdateIsSkipped() throws MeedlException {
        firstLoanee.setCreditScoreUpdatedAt(LocalDateTime.now().minusDays(10));
        when(loaneeOutputPort.findLoaneeById(mockId)).thenReturn(firstLoanee);

        Loanee result = loaneeService.viewLoaneeDetails(mockId);

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

}







