package africa.nkwadoma.nkwadoma.domain.service.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class CohortServiceTest {
    @InjectMocks
    private CohortService cohortService;
    private Cohort elites;
    private Cohort xplorers;
    @Mock
    private CohortOutputPort cohortOutputPort;
    private String mockId = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private int pageSize = 2;
    private int pageNumber = 0;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private Program program;



    @BeforeEach
    void setUp() {
        elites = new Cohort();
        elites.setProgramId(mockId);
        elites.setName("Elite");
        elites.setCreatedBy(mockId);
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setProgramId(mockId);
        xplorers.setCreatedBy(mockId);
        xplorers.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        xplorers.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
    }

    @Test
    void saveCohort() {
        try {
            when(cohortOutputPort.saveCohort(elites)).thenReturn(elites);
            Cohort cohort = cohortService.createOrEditCohort(elites);
            assertEquals(cohort.getName(), elites.getName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveCohortWithExistingCohortName() {
        try {
            when(cohortOutputPort.saveCohort(xplorers)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertThrows(MeedlException.class,() -> cohortService.createOrEditCohort(xplorers));
    }


    @Test
    void saveAnotherCohortInProgram() {
        try {
            when(cohortOutputPort.saveCohort(elites)).thenReturn(elites);
            when(cohortOutputPort.saveCohort(xplorers)).thenReturn(xplorers);

            Cohort cohort = cohortService.createOrEditCohort(elites);
            Cohort secondCohort = cohortService.createOrEditCohort(xplorers);

            assertEquals(secondCohort.getName(), xplorers.getName());
            assertEquals(cohort.getName(), elites.getName());

            verify(cohortOutputPort, times(2)).saveCohort(any());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void viewCohortDetails(){
        try{
            when(cohortOutputPort.viewCohortDetails(mockId,mockId,mockId)).thenReturn(xplorers);
            Cohort cohort = cohortService
                    .viewCohortDetails(mockId,mockId, mockId);
            assertNotNull(cohort);
            assertEquals(cohort.getName(),xplorers.getName());
            verify(cohortOutputPort, times(1)).viewCohortDetails(any(), any(), any());
        }catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void viewCohortWithNullUserId(){
        try {
            when(cohortOutputPort.viewCohortDetails(null,mockId, mockId)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, () -> cohortService.viewCohortDetails(null,
                    mockId, mockId));
        } catch (MeedlException e) {
            log.error("Failed {}", e.getMessage());
        }
    }
    @Test
    void viewCohortWithNullProgramId(){
        try {
            when(cohortOutputPort.viewCohortDetails(mockId,null, mockId )).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, () -> cohortService.viewCohortDetails(mockId,
                    null,mockId));
        } catch (MeedlException e) {
            log.error("Failed {}", e.getMessage());
        }
    }

    @Test
    void viewCohortWithNullCohortId() {
        try {
            when(cohortOutputPort.viewCohortDetails(mockId, mockId, null)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, () -> cohortService.viewCohortDetails(mockId,
                    mockId, null));
        } catch (MeedlException e) {
            log.error("Failed {}", e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyUserId(String userId){
        try {
            when(cohortOutputPort.viewCohortDetails(userId, mockId, mockId)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertThrows(MeedlException.class, ()->
                cohortService.viewCohortDetails(userId,
                        mockId,
                        mockId));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyProgramId(String programId){
        try {
            when(cohortOutputPort.viewCohortDetails(mockId, programId , mockId)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertThrows(MeedlException.class, ()->
                cohortService.viewCohortDetails(mockId,
                        programId,
                        mockId));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyCohortId(String cohortId){
        try {
            when(cohortOutputPort.viewCohortDetails(mockId, mockId, cohortId)).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertThrows(MeedlException.class, ()->
                cohortService.viewCohortDetails(mockId,
                        mockId,
                        cohortId));
    }

    @Test
    void viewAllCohortInAProgram() {
        List<Cohort> foundCohorts = List.of(xplorers, elites);

        Page<Cohort> allCohortInAProgram = new PageImpl<>(foundCohorts);
        try {
            when(cohortOutputPort.findAllCohortInAProgram(mockId))
                    .thenReturn(foundCohorts);
            allCohortInAProgram = cohortService.viewAllCohortInAProgram(mockId, pageSize, pageNumber);
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        List<Cohort> cohorts = allCohortInAProgram.toList();
        assertEquals(2, cohorts.size());
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE, "ndjnhfd,"})
    void deleteCohortWithInvalidId(String cohortId){
        assertThrows(MeedlException.class, ()-> cohortService.deleteCohort(cohortId));
    }
    @Test
    void deleteCohort(){
        try {
            when(cohortOutputPort.viewCohortDetails(mockId,mockId,mockId)).thenReturn(xplorers);
            Cohort cohort = cohortService.viewCohortDetails(mockId,mockId, mockId);
            assertNotNull(cohort);

            doNothing().when(cohortOutputPort).deleteCohort(mockId);
            cohortService.deleteCohort(mockId);

            doThrow(MeedlException.class).when(cohortOutputPort).viewCohortDetails(mockId, mockId, mockId);
            assertThrows(MeedlException.class, ()-> cohortService.viewCohortDetails(mockId,mockId,mockId));
        } catch (MeedlException e) {
            log.error("Error deleting cohort {}",e.getMessage());
        }
    }

    @Test
    void cannotEditCohortWithLoanDetails() {
        try{
            Cohort elites = new Cohort();
            elites.setId(mockId);
            elites.setCohortLoanDetail(new CohortLoanDetail());
            when(cohortOutputPort.saveCohort(elites)).thenThrow( MeedlException.class);
            assertThrows(MeedlException.class, () -> cohortService.createOrEditCohort(elites));
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
    }

    @Test
    void cohortWithoutLoanDetailsCanBeEdited() {
        try {
            Cohort elites = new Cohort();
            elites.setId(mockId);
            elites.setName("edited cohort");
            when(cohortOutputPort.saveCohort(elites)).thenReturn(elites);
            Cohort editedCohort = cohortService.createOrEditCohort(elites);
            assertEquals("edited cohort", editedCohort.getName());
        }catch (MeedlException e){
            log.error("{}", e.getMessage());
        }

    }

    @Order(9)
    @Test
    void addLoanDetailsToCohort() {
       try{
            CohortLoanDetail cohortLoanDetail = getCohortLoanDetail();
            elites.setCohortLoanDetail(cohortLoanDetail);
            when(cohortOutputPort.saveCohort(elites)).thenReturn(elites);
            Cohort editedCohort = cohortService.createOrEditCohort(elites);
            assertNotNull(editedCohort.getCohortLoanDetail());
       }catch (MeedlException e){
           log.error("{}", e.getMessage());
       }
    }

    private static CohortLoanDetail getCohortLoanDetail() {
        CohortLoanDetail cohortLoanDetail = new CohortLoanDetail();
        LoanDetail loanDetail = new LoanDetail();
        loanDetail.setDebtPercentage(0.34);
        loanDetail.setRepaymentPercentage(0.67);
        loanDetail.setMonthlyExpected(BigDecimal.valueOf(450));
        loanDetail.setTotalAmountRepaid(BigDecimal.valueOf(500));
        loanDetail.setTotalInterestIncurred(BigDecimal.valueOf(600));
        loanDetail.setLastMonthActual(BigDecimal.valueOf(200));
        loanDetail.setTotalAmountDisbursed(BigDecimal.valueOf(50000));
        loanDetail.setTotalOutstanding(BigDecimal.valueOf(450));
        cohortLoanDetail.setLoanDetail(loanDetail);
        return cohortLoanDetail;
    }

}

