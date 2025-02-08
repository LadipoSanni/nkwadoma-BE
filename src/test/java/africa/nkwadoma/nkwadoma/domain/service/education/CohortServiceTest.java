package africa.nkwadoma.nkwadoma.domain.service.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.CohortMapper;
import africa.nkwadoma.nkwadoma.test.data.TestData;
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

import java.math.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    private OrganizationIdentity organizationIdentity;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private ServiceOffering serviceOffering;
    private UserIdentity userIdentity;


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
        programCohort = new ProgramCohort();
        programCohort.setCohort(elites);
        programCohort.setProgramId(program.getId());

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setProgramId(program.getId());
        xplorers.setCreatedBy(mockId);
        xplorers.setStartDate(LocalDate.of(2024,1,2));
        xplorers.setExpectedEndDate(LocalDate.of(2024,8,2));
        xplorers.setTuitionAmount(BigDecimal.valueOf(2000));

        loanBreakdown = new LoanBreakdown();
        loanBreakdown.setLoanBreakdownId(mockId+"e");
        loanBreakdown.setCohort(elites);
        loanBreakdown.setItemName("juno");
        loanBreakdown.setItemAmount(BigDecimal.valueOf(3000));
        loanBreakdown.setCurrency("usd");

    }

    @Test
    void saveCohort() {
        try {
            elites.setLoanBreakdowns(List.of(loanBreakdown));
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.save(elites)).thenReturn(elites);
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
            xplorers.setLoanBreakdowns(List.of(loanBreakdown));
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.save(xplorers)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertThrows(MeedlException.class,() -> cohortService.createCohort(xplorers));
    }


    @Test
    void saveCohortWithNegativeLoanBreakDownItemAmount(){
        loanBreakdown.setItemAmount(BigDecimal.valueOf(-2000));
        xplorers.setLoanBreakdowns(List.of(loanBreakdown));
        assertThrows(MeedlException.class,() -> cohortService.createCohort(xplorers));
    }

    @Test
    void saveAnotherCohortInProgram() {
        try {
            xplorers.setLoanBreakdowns(List.of(loanBreakdown));
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            when(cohortOutputPort.save(xplorers)).thenReturn(xplorers);
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
            when(cohortOutputPort.viewCohortDetails(mockId,mockId)).thenReturn(xplorers);
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            Cohort cohort = cohortService
                    .viewCohortDetails(mockId, mockId);
            assertNotNull(cohort);
            assertEquals(cohort.getName(),xplorers.getName());
            verify(cohortOutputPort, times(1)).viewCohortDetails(any(), any());
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
        List<Cohort> searchedCohort = new ArrayList<>();
        try{
            when(cohortOutputPort.searchForCohortInAProgram(anyString(), eq(xplorers.getProgramId())))
                    .thenReturn(List.of(elites, xplorers));

            searchedCohort = cohortService.searchForCohortInAProgram("x", xplorers.getProgramId());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }

        assertNotNull(searchedCohort);
        assertEquals(2, searchedCohort.size());
    }

    @Test
    void viewAllCohortInAProgram() {
        try {
            Page<Cohort> allCohortInAProgram = cohortService.viewAllCohortInAProgram(program.getId(),pageNumber,pageSize);
            List<Cohort> cohorts = allCohortInAProgram.toList();

            assertEquals(2, cohorts.size());
            verify(cohortOutputPort, times(1)).findAllCohortInAProgram(program.getId(),pageNumber,pageSize);
        }
        catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void viewCohortsInAProgramWithNullProgramId(){
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(null,pageNumber,pageSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid uuid"})
    void viewCohortsInAProgramWithNonUUIDProgramId(String programId) {
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(programId,pageNumber,pageSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3a6d1124-1349-4f5b-831a-ac269369a90f"})
    void viewCohortsInAProgramWithInvalidProgramId(String programId){
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(programId,pageNumber,pageSize));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void viewCohortsInAProgramWithInvalidPageSize(int pageSize) {
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(program.getId(),pageNumber,pageSize));
    }


    @ParameterizedTest
    @ValueSource(ints = {-1})
    void viewCohortsInAProgramWithInvalidPageNumber(int pageNumber){
        assertThrows(MeedlException.class, ()-> cohortService.viewAllCohortInAProgram(program.getId(),pageNumber,pageSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1de71eaa-de6d-4cdf-8f93-aa7be533f4aa     ",
            "      1de71eaa-de6d-4cdf-8f93-aa7be533f4aa",
            "    1de71eaa-de6d-4cdf-8f93-aa7be533f4aa     "
    })
    void viewCohortsInAProgramWithProgramIdWithSpaces(String programId){
        try {
            Page<Cohort> allCohortInAProgram = cohortService.viewAllCohortInAProgram(programId,pageNumber,pageSize);
            List<Cohort> cohorts = allCohortInAProgram.toList();

            assertEquals(2, cohorts.size());
            verify(cohortOutputPort, times(1)).findAllCohortInAProgram(programId.trim(),pageNumber,pageSize);
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
    void deleteCohort(){
        try {
            when(cohortOutputPort.viewCohortDetails(mockId,mockId)).thenReturn(xplorers);
            when(programOutputPort.findProgramById(mockId)).thenReturn(program);
            Cohort cohort = cohortService.viewCohortDetails(mockId, mockId);
            assertNotNull(cohort);

            doNothing().when(cohortOutputPort).deleteCohort(mockId);
            cohortService.deleteCohort(mockId);

            doThrow(MeedlException.class).when(cohortOutputPort).viewCohortDetails(mockId, mockId);
            assertThrows(MeedlException.class, ()-> cohortService.viewCohortDetails(mockId,mockId));
        } catch (MeedlException e) {
            log.error("Error deleting cohort {}",e.getMessage());
        }
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
            when(cohortOutputPort.findCohort(elites.getId())).thenReturn(elites);
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

