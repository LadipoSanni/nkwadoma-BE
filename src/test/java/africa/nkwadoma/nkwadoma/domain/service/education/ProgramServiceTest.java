package africa.nkwadoma.nkwadoma.domain.service.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ProgramLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.education.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProgramServiceTest {
    @InjectMocks
    private ProgramService programService;
    @Mock
    private ProgramOutputPort programOutputPort;
    @Mock
    private ProgramMapper programMapper;
    @Mock
    private UserIdentityOutputPort userIdentityOutputPort;
    @Mock
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    @Mock
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private Program program;
    private int pageSize = 10;
    private int pageNumber = 0;
    private String testId = "1de71eaa-de6d-4cdf-8f93-aa7be533f4aa";
    UserIdentity userIdentity;
    OrganizationEmployeeIdentity employeeIdentity;
    OrganizationIdentity organizationIdentity;
    @Mock
    private ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    private ProgramLoanDetail programLoanDetail;
    @Mock
    private  LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Mock
    private  CohortLoanDetailOutputPort cohortLoanDetailOutputPort;
    @Mock
    private  CohortOutputPort cohortOutputPort;
    @Mock
    private InstituteMetricsOutputPort instituteMetricsOutputPort;
    private InstituteMetrics instituteMetrics;

    @BeforeEach
    void setUp() {
        userIdentity = UserIdentity.builder().role(IdentityRole.ORGANIZATION_ADMIN).
                id(testId).createdBy(testId).build();
        employeeIdentity = OrganizationEmployeeIdentity.builder().meedlUser(userIdentity).
                organization("29a416b3-ab47-47d3-8ea0-007437b700f1").build();
        program = Program.builder().id(testId).name("Ben's-Mat").durationType(DurationType.YEARS)
                .organizationId("66d4f7e4-7f60-46d5-b55d-0383630a1fc2").
                programDescription("A great program").programStatus(ActivationStatus.ACTIVE).
                createdBy(testId).deliveryType(DeliveryType.ONSITE).
                mode(ProgramMode.FULL_TIME).duration(BigInteger.ONE.intValue()).build();
        organizationIdentity = TestData.createOrganizationTestData("organization","RC12345678",List.of(employeeIdentity));
        organizationIdentity.setId(testId);
        programLoanDetail = TestData.buildProgramLoanDetail(program);
        instituteMetrics = TestData.createInstituteMetrics(organizationIdentity);
    }

    @Test
    void addProgram() {
        try {
            when(programOutputPort.findCreatorOrganization(program.getCreatedBy())).thenReturn(OrganizationIdentity.builder().build());
            when(programOutputPort.programExistsInOrganization(program)).thenReturn(false);
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            when(programLoanDetailOutputPort.save(any(ProgramLoanDetail.class))).thenReturn(programLoanDetail);
            when(instituteMetricsOutputPort.findByOrganizationId(any())).thenReturn(instituteMetrics);
            when(instituteMetricsOutputPort.save(any(InstituteMetrics.class))).thenReturn(instituteMetrics);

            Program addedProgram = programService.createProgram(program);
            verify(programOutputPort, times(1)).saveProgram(program);

            assertEquals(addedProgram.getProgramDescription(), program.getProgramDescription());
            assertEquals(addedProgram.getDurationType(), program.getDurationType());
            assertEquals(addedProgram.getName(), program.getName());
            assertEquals(addedProgram.getProgramStatus(), program.getProgramStatus());
            assertEquals(addedProgram.getDuration(), program.getDuration());
            assertEquals(addedProgram.getMode(), program.getMode());
            assertEquals(addedProgram.getCreatedAt(), program.getCreatedAt());
        } catch (MeedlException e) {
            log.info("Error creating program: {}", e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "121323","#ndj", "(*^#()@", "Haus*&^"})
    void createProgramWithInvalidName(String programName){
        program = new Program();
        program.setName(programName);
        assertThrows(MeedlException.class, ()-> programService.createProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void addProgramWithInvalidCreatorId(String createdBy) {
        program.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()->programService.createProgram(program));
    }
    @Test
    void addProgramWithExistingName() {
        try {
            when(programOutputPort.findCreatorOrganization(program.getCreatedBy())).thenReturn(OrganizationIdentity.builder().build());
            when(programOutputPort.programExistsInOrganization(program)).thenReturn(false);
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            when(programLoanDetailOutputPort.save(any(ProgramLoanDetail.class))).thenReturn(programLoanDetail);
            when(instituteMetricsOutputPort.findByOrganizationId(any())).thenReturn(instituteMetrics);
            when(instituteMetricsOutputPort.save(any(InstituteMetrics.class))).thenReturn(instituteMetrics);

            Program createdProgram = programService.createProgram(program);
            assertNotNull(createdProgram);
            verify(programOutputPort, times(1)).saveProgram(program);
            when(programOutputPort.saveProgram(program)).thenThrow(MeedlException.class);
            assertThrows(MeedlException.class, ()-> programService.createProgram(program));
        } catch (MeedlException e) {
            log.error("Error creating program", e);
        }
    }


    @Test
    void updateProgram() {
        try {

            when(programOutputPort.findProgramById(program.getId())).thenReturn(program);
//            when(programMapper.updateProgram(addedProgram, program)).thenReturn(void);
            when(programOutputPort.saveProgram(program)).thenReturn(program);
            Program updatedProgram = programService.updateProgram(program);

            verify(programOutputPort, times(1)).saveProgram(program);
            assertNotNull(updatedProgram);
            assertEquals(updatedProgram.getProgramDescription(), program.getProgramDescription());
        } catch (MeedlException e) {
            log.error("Error updating program", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void updateProgramWithEmptyProgramId(String programId) {
        program.setId(programId);
        assertThrows(MeedlException.class, ()->programService.updateProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"4089874209", "non-uuid"})
    void updateProgramWithNonUUIDProgramId(String programId) {
        program.setId(programId);
        assertThrows(MeedlException.class, ()->programService.updateProgram(program));
    }

    @Test
    void updateProgramWithNullProgramId() {
        program.setId(null);
        assertThrows(MeedlException.class, () -> programService.updateProgram((program)));
    }

    @Test
    void updateProgramWithNullProgram() {
        MeedlException exception = assertThrows(MeedlException.class, () -> programService.updateProgram((null)));
        assertEquals(exception.getMessage(), ProgramMessages.PROGRAM_CANNOT_BE_EMPTY.getMessage());
    }
    @Test
    void createProgramWithNonExistingCreatedBy() {
        program.setCreatedBy("f2a25ed8-a594-4cb4-a2fb-8e0dcca72f71");
        try {
            when( programOutputPort.findCreatorOrganization(program.getCreatedBy())).thenThrow(MeedlException.class);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, () -> programService.createProgram(program));
    }
    @Test
    void viewAllPrograms() {
        try {
            program.setCreatedBy(testId);
            when(userIdentityOutputPort.findById(program.getCreatedBy())).thenReturn(userIdentity);
            when(programOutputPort.findAllPrograms(testId, pageSize, pageNumber)).
                    thenReturn(new PageImpl<>(List.of(program)));
            program.setPageSize(pageSize);
            program.setPageNumber(pageNumber);
            program.setCreatedBy(testId);
            Page<Program> programs = programService.viewAllPrograms(program);
            List<Program> programsList = programs.toList();

            verify(programOutputPort, times(1)).
                    findAllPrograms(testId, pageSize, pageNumber);
            assertNotNull(programs);
            assertNotNull(programsList);
            assertEquals(programsList.get(0).getId(), program.getId());
            assertEquals(programsList.get(0).getName(), program.getName());
            assertEquals(programsList.get(0).getDuration(), program.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), program.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfLoanees(), program.getNumberOfLoanees());
            assertEquals(BigDecimal.ZERO, programsList.get(0).getTotalAmountDisbursed());
            assertEquals(BigDecimal.ZERO, programsList.get(0).getTotalAmountOutstanding());
            assertEquals(BigDecimal.ZERO, programsList.get(0).getTotalAmountRepaid());
        } catch (MeedlException e) {
            log.error("Error viewing all programs", e);
        }
    }

    @Test
     void searchProgramByNameWithinAnOrganization() {
        try {
            program.setPageNumber(pageNumber);
            program.setPageSize(pageSize);
            when(userIdentityOutputPort.findById(program.getCreatedBy())).thenReturn(userIdentity);
            when(employeeIdentityOutputPort.findByCreatedBy(program.getId())).thenReturn(employeeIdentity);
            when(programOutputPort.findProgramByNameWithinOrganization(program, employeeIdentity.getOrganization()))
                    .thenReturn(new PageImpl<>(List.of(program)));
            Page<Program> foundProgram = programService.searchProgramByName(program);

            assertNotNull(foundProgram);
            assertEquals(foundProgram, new PageImpl<>(List.of(program)));
            verify(programOutputPort, times(1)).findProgramByNameWithinOrganization(program, employeeIdentity.getOrganization());
        } catch (MeedlException e) {
            log.error("Error viewing program by name", e);
        }
    }

    @Test
    void searchProgramAcrossPlatform() {
        try {
            program.setPageNumber(pageNumber);
            program.setPageSize(pageSize);
            userIdentity.setRole(IdentityRole.PORTFOLIO_MANAGER);
            when(userIdentityOutputPort.findById(program.getCreatedBy())).thenReturn(userIdentity);
            when(programOutputPort.findProgramByName(program.getName(),program.getPageNumber(),program.getPageSize()))
                    .thenReturn(new PageImpl<>(List.of(program)));
                    Page<Program> foundProgram = programService.searchProgramByName(program);
            assertNotNull(foundProgram);
            verify(programOutputPort, times(1)).findProgramByName(program.getName(),program.getPageNumber(),program.getPageSize());
        } catch (MeedlException e) {
            log.error("Error viewing program by name", e);
        }
    }


    @Test
    void deleteProgramWithActiveLoaneeInCohort() throws MeedlException {
        when(programOutputPort.findProgramById(program.getId())).thenReturn(program);
        when(programOutputPort.checkIfLoaneeExistInProgram(program.getId())).thenReturn(true);
        assertThrows(MeedlException.class, ()->programService.deleteProgram(program));
    }

    @Test
    void deleteProgramWithNoActiveLoaneeInCohort()  {
        try {
            organizationIdentity.setNumberOfPrograms(5);
            organizationIdentity.setNumberOfCohort(10);
            when(programOutputPort.findProgramById(program.getId())).thenReturn(program);
            when(programOutputPort.checkIfLoaneeExistInProgram(program.getId())).thenReturn(false);
            doNothing().when(loanBreakdownOutputPort).deleteAllBreakDownAssociateWithProgram(program.getId());
            doNothing().when(cohortLoanDetailOutputPort).deleteAllCohortLoanDetailAssociateWithProgram(program.getId());
            when(cohortOutputPort.deleteAllCohortAssociateWithProgram(program.getId())).thenReturn(1);
            doNothing().when(programOutputPort).deleteProgram(program.getId());
            when(organizationIdentityOutputPort.findById(program.getOrganizationId())).thenReturn(organizationIdentity);
            when(organizationIdentityOutputPort.save(organizationIdentity)).thenReturn(organizationIdentity);

            assertDoesNotThrow(() -> programService.deleteProgram(program));

            verify(programOutputPort).findProgramById(program.getId());
            verify(programOutputPort).checkIfLoaneeExistInProgram(program.getId());
            verify(loanBreakdownOutputPort).deleteAllBreakDownAssociateWithProgram(program.getId());
            verify(cohortLoanDetailOutputPort).deleteAllCohortLoanDetailAssociateWithProgram(program.getId());
            verify(cohortOutputPort).deleteAllCohortAssociateWithProgram(program.getId());
            verify(programOutputPort).deleteProgram(program.getId());
            verify(organizationIdentityOutputPort).findById(program.getOrganizationId());
            verify(organizationIdentityOutputPort).save(organizationIdentity);
        }catch (MeedlException meedlException){
            log.error("Error deleting program", meedlException);
        }

        assertEquals(4, organizationIdentity.getNumberOfPrograms(), "Number of programs should be decremented by 1");
        assertEquals(9, organizationIdentity.getNumberOfCohort(), "Number of cohorts should be decremented by 1");

    }



    @Test
    void deleteNonExistingProgram() {
        program.setId("non existing id");
        assertThrows(MeedlException.class, ()->programService.deleteProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"non-uuid"})
    void deleteProgramWithNonUUIDId(String programId) {
        program.setId(programId);
        assertThrows(MeedlException.class, ()->programService.deleteProgram(program));
    }

    @Test
    void deleteProgramWithNullId() {
        program.setId(null);
        assertThrows(MeedlException.class, ()->programService.deleteProgram(program));
    }

    @Test
    void deleteNullProgram() {
        assertThrows(MeedlException.class, ()->programService.deleteProgram(null));
    }

    @Test
    void viewProgramById() {
        try {
            program.setId("1de71eaa-de6d-4cdf-8f93-aa7be533f4aa");
            when(programOutputPort.findProgramById(program.getId())).thenReturn(program);
            when(programLoanDetailOutputPort.findByProgramId(program.getId())).thenReturn(programLoanDetail);
            Program foundProgram = programService.viewProgramById(program);
            assertNotNull(foundProgram);
            assertEquals(foundProgram, program);
            verify(programOutputPort, times(1)).findProgramById(program.getId());
        } catch (MeedlException e) {
            log.error("Error viewing program by name", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE})
    void viewProgramWithEmptyId(String programId) {
        program.setId(programId);
        assertThrows(MeedlException.class, ()-> programService.viewProgramById(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"non-uuid", "3657679"})
    void viewProgramWithNonUUIDId(String programId) {
        program.setId(programId);
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programService.viewProgramById(program));
        assertEquals("Please provide a valid program identification.", meedlException.getMessage());
    }

    @Test
    void viewNullProgram() {
        assertThrows(MeedlException.class, ()-> programService.viewProgramById(null));
    }

    @Test
    void viewProgramWithNullId() {
        program.setId(null);
        assertThrows(MeedlException.class, ()-> programService.viewProgramById(program));
    }
}