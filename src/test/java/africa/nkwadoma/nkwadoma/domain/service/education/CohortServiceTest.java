package africa.nkwadoma.nkwadoma.domain.service.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanOfferOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.*;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class CohortServiceTest {
    @InjectMocks
    private CohortService cohortService;
    private Cohort elites;
    private Cohort xplorers;
    @Mock
    private CohortOutputPort cohortOutputPort;
    private String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private int pageSize = 2;
    private int pageNumber = 0;
    private Program program;
    @Mock
    private ProgramOutputPort programOutputPort;
    @Mock
    private ProgramCohortOutputPort programCohortOutputPort;
    @Mock
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    private ProgramCohort programCohort ;
    private LoanBreakdown loanBreakdown;
    @Mock
    private CohortMapper cohortMapper;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    private OrganizationIdentity organizationIdentity;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private ServiceOffering serviceOffering;
    private UserIdentity userIdentity;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    private CohortLoanDetail cohortLoanDetail;
    @Mock
    private LoanOfferOutputPort loanOfferOutputPort;


    @BeforeEach
    void setUp() {
        userIdentity = TestData.createTestUserIdentity("qudusa55@gmail.com");
        organizationEmployeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(userIdentity);
        organizationIdentity =
                TestData.createOrganizationTestData("testOrg1","rc34rhchjdg",List.of(organizationEmployeeIdentity));

        program = Program.builder().id(mockId).name("My program").durationType(DurationType.YEARS).
                programDescription("A great program").programStatus(ActivationStatus.ACTIVE).organizationId(organizationIdentity.getId()).
                createdBy("875565").deliveryType(DeliveryType.ONSITE).organizationIdentity(organizationIdentity).
                mode(ProgramMode.FULL_TIME).duration(7).build();

        elites = new Cohort();
        elites.setId(mockId);
        elites.setProgramId(program.getId());
        elites.setName("x-man");
        elites.setCreatedBy(mockId);
        elites.setStartDate(LocalDate.of(2024,11,29));
        elites.setExpectedEndDate(LocalDate.of( 2025,6,29));
        elites.setTuitionAmount(BigDecimal.valueOf(2000));
        elites.setCohortStatus(CohortStatus.GRADUATED);
        programCohort = new ProgramCohort();
        programCohort.setCohort(elites);
        programCohort.setProgramId(program.getId());

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setProgramId(program.getId());
        xplorers.setCreatedBy(mockId);
        xplorers.setStartDate(LocalDate.of(2024,1,2));
        xplorers.setCohortStatus(CohortStatus.GRADUATED);
        xplorers.setExpectedEndDate(LocalDate.of(2024,8,2));
        xplorers.setTuitionAmount(BigDecimal.valueOf(2000));

        loanBreakdown = new LoanBreakdown();
        loanBreakdown.setLoanBreakdownId(mockId+"e");
        loanBreakdown.setCohort(elites);
        loanBreakdown.setItemName("juno");
        loanBreakdown.setItemAmount(BigDecimal.valueOf(3000));
        loanBreakdown.setCurrency("usd");

        cohortLoanDetail = TestData.buildCohortLoanDetail(elites);

    }

    @Test
    void saveCohort() {
        try {
            elites.setLoanBreakdowns(List.of(loanBreakdown));
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.save(elites)).thenReturn(elites);
            when(organizationIdentityOutputPort.findById(program.getOrganizationId())).thenReturn(organizationIdentity);
            when(cohortLoanDetailOutputPort.save(any())).thenReturn(cohortLoanDetail);
            Cohort cohort = cohortService.createCohort(elites);
            assertEquals(cohort.getName(), elites.getName());
            assertEquals(LocalDate.of(2025,6,29),cohort.getExpectedEndDate());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveCohortWithExistingCohortName() {
        try {
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            xplorers.setLoanBreakdowns(List.of(loanBreakdown));
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.save(xplorers)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertThrows(MeedlException.class,() -> cohortService.createCohort(xplorers));
    }


    @Test
    void saveCohortWithNegativeLoanBreakDownItemAmount() throws MeedlException {
        when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
        loanBreakdown.setItemAmount(BigDecimal.valueOf(-2000));
        xplorers.setLoanBreakdowns(List.of(loanBreakdown));
        assertThrows(MeedlException.class,() -> cohortService.createCohort(xplorers));
    }

    @Test
    void saveAnotherCohortInProgram() {
        try {
            cohortLoanDetail.setCohort(xplorers);
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            xplorers.setLoanBreakdowns(List.of(loanBreakdown));
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.save(xplorers)).thenReturn(xplorers);
            when(organizationIdentityOutputPort.findById(program.getOrganizationId())).thenReturn(organizationIdentity);
            when(cohortLoanDetailOutputPort.save(any())).thenReturn(cohortLoanDetail);
            Cohort cohort = cohortService.createCohort(xplorers);
            assertEquals(cohort.getName(), xplorers.getName());
            verify(cohortOutputPort, times(2)).save(any());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void viewCohortDetails(){
        try{
            xplorers.setId(mockId);
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(xplorers);
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortLoanDetailOutputPort.findByCohortId(elites.getId())).thenReturn(cohortLoanDetail);
            when(loanOfferOutputPort.countNumberOfPendingLoanOfferForCohort(xplorers.getId())).thenReturn(2);
            Cohort cohort = cohortService
                    .viewCohortDetails(mockId, mockId);
            assertNotNull(cohort);
            assertEquals(cohort.getName(),xplorers.getName());
            verify(cohortOutputPort, times(1)).findCohortById(any());
        }catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void viewCohortWithNullUserId(){
        assertThrows(MeedlException.class, () -> cohortService.viewCohortDetails(null, mockId));
    }

    @Test
    void viewCohortWithNullCohortId() {
            assertThrows(MeedlException.class, () -> cohortService.viewCohortDetails(mockId, null));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyUserId(String userId){
        assertThrows(MeedlException.class, ()->
                cohortService.viewCohortDetails(userId,
                        mockId));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyCohortId(String cohortId){
        assertThrows(MeedlException.class, ()->
                cohortService.viewCohortDetails(mockId,
                        cohortId));
    }

    @Order(6)
    @Test
    void searchForCohort() {
        Page<Cohort> searchedCohort = new PageImpl<>(List.of(elites, xplorers));
        Cohort cohort = Cohort.builder().name("x").pageNumber(0).
                pageSize(10).programId(xplorers.getProgramId()).build();
        try {
            userIdentity.setRole(IdentityRole.ORGANIZATION_ADMIN);
            when(userIdentityOutputPort.findById(mockId)).thenReturn(userIdentity);
            when(cohortOutputPort.searchForCohortInAProgram(
                    anyString(),
                    eq(cohort.getProgramId()),
                    eq(cohort.getPageSize()),
                    eq(cohort.getPageNumber())
            )).thenReturn(searchedCohort);

            searchedCohort = cohortService.searchForCohort(mockId,cohort);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

        assertNotNull(searchedCohort);
        assertEquals(2, searchedCohort.getContent().size());
    }

    @Test
    void viewAllCohortInAProgram() {
        try {

            Page<Cohort> allCohortInAProgram = cohortService.viewAllCohortInAProgram(elites);
            List<Cohort> cohorts = allCohortInAProgram.toList();

            assertEquals(2, cohorts.size());
            verify(cohortOutputPort, times(1)).findAllCohortInAProgram(elites);
        }
        catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void viewCohortsInAProgramWithNullProgramId(){
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid uuid"})
    void viewCohortsInAProgramWithNonUUIDProgramId(String programId) {
        elites.setProgramId(programId);
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(elites));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3a6d1124-1349-4f5b-831a-ac269369a90f"})
    void viewCohortsInAProgramWithInvalidProgramId(String programId){
        elites.setProgramId(programId);
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(elites));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void viewCohortsInAProgramWithInvalidPageSize(int pageSize) {
        elites.setPageSize(pageSize);
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(elites));
    }


    @ParameterizedTest
    @ValueSource(ints = {-1})
    void viewCohortsInAProgramWithInvalidPageNumber(int pageNumber){
        elites.setPageNumber(pageNumber);
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(elites));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1de71eaa-de6d-4cdf-8f93-aa7be533f4aa     ",
            "      1de71eaa-de6d-4cdf-8f93-aa7be533f4aa",
            "    1de71eaa-de6d-4cdf-8f93-aa7be533f4aa     "
    })
    void viewCohortsInAProgramWithProgramIdWithSpaces(String programId){
        elites.setProgramId(programId);
        try {
            Page<Cohort> allCohortInAProgram = cohortService.viewAllCohortInAProgram(elites);
            List<Cohort> cohorts = allCohortInAProgram.toList();

            assertEquals(2, cohorts.size());
            verify(cohortOutputPort, times(1)).findAllCohortInAProgram(elites);
        }
        catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE, "ndjnhfd,"})
    void deleteCohortWithInvalidId(String cohortId){
        assertThrows(MeedlException.class, ()-> cohortService.deleteCohort(cohortId));
    }
    @Test
    void deleteCohort() {
        try {
            when(loaneeOutputPort.findAllLoaneesByCohortId(mockId)).thenReturn(new ArrayList<>());
            xplorers.setId(mockId);
            when(cohortOutputPort.findCohortById(mockId)).thenReturn(xplorers);
            when(programOutputPort.findProgramById(xplorers.getProgramId())).thenReturn(program);
            when(organizationIdentityOutputPort.findById(program.getOrganizationId()))
                    .thenReturn(organizationIdentity);
            program.setNumberOfCohort(1);
            organizationIdentity.setNumberOfCohort(1);
            cohortService.deleteCohort(mockId);
            verify(cohortOutputPort).deleteCohort(xplorers.getId());
            verify(programOutputPort).saveProgram(program);
            verify(organizationIdentityOutputPort).save(organizationIdentity);
        } catch (MeedlException e) {
            log.error("Error deleting cohort {}",e.getMessage());
        }
        assertEquals(0, program.getNumberOfCohort());
        assertEquals(0, organizationIdentity.getNumberOfCohort());
    }

    @Test
    void deleteCohort_withLoanees_throwsException() throws MeedlException {
        List<Loanee> loanees = List.of(new Loanee());
        when(loaneeOutputPort.findAllLoaneesByCohortId(mockId)).thenReturn(loanees);
        assertThrows(EducationException.class, () ->
                cohortService.deleteCohort(mockId));
        verify(cohortOutputPort, never()).deleteCohort(anyString());
        assertEquals(0, program.getNumberOfCohort());
        assertEquals(0, organizationIdentity.getNumberOfCohort());
    }

    @Test
    void cannotEditCohortWithLoanDetails() {
        elites.setLoanDetail(getLoanDetail());
        assertThrows(MeedlException.class, () -> cohortService.editCohort(elites));
    }

    @Test
    void cohortWithoutLoanDetailsCanBeEdited() {
        try {
            elites.setId(mockId);
            elites.setName("edited cohort");
            elites.setUpdatedBy(mockId);
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.findCohortById(elites.getId())).thenReturn(elites);
            when(cohortOutputPort.checkIfCohortExistWithName(elites.getName())).thenReturn(elites);
            when(loanBreakdownOutputPort.findAllByCohortId(elites.getId())).thenReturn(List.of(loanBreakdown));
            when(cohortOutputPort.save(elites)).thenReturn(elites);
            Cohort editedCohort = cohortService.editCohort(elites);
            assertEquals("edited cohort", editedCohort.getName());
        }catch (MeedlException e){
            log.error("{}", e.getMessage());
        }

    }

    private static LoanDetail getLoanDetail() {
        LoanDetail loanDetail = new LoanDetail();
        loanDetail.setDebtPercentage(0.34);
        loanDetail.setRepaymentPercentage(0.67);
        loanDetail.setMonthlyExpected(BigDecimal.valueOf(450));
        loanDetail.setTotalAmountRepaid(BigDecimal.valueOf(500));
        loanDetail.setTotalInterestIncurred(BigDecimal.valueOf(600));
        loanDetail.setLastMonthActual(BigDecimal.valueOf(200));
        loanDetail.setTotalAmountDisbursed(BigDecimal.valueOf(50000));
        loanDetail.setTotalOutstanding(BigDecimal.valueOf(450));
        return loanDetail;
    }

}

