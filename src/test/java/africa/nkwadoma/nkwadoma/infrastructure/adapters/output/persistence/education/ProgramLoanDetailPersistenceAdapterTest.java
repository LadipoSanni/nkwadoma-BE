package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static africa.nkwadoma.nkwadoma.domain.enums.ServiceOfferingType.TRAINING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class ProgramLoanDetailPersistenceAdapterTest {


    @Autowired
    private InstituteMetricsOutputPort instituteMetricsOutputPort;
    private UserIdentity userIdentity;
    private UserIdentity meedleUser;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private Program program;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private ProgramLoanDetail programLoanDetail;
    @Autowired
    private ProgramLoanDetailOutputPort programLoanDetailOutputPort;
    private String loanDetailsId;



    @BeforeAll
    void setUpCohortLoanee() {
        try {
            meedleUser = TestData.createTestUserIdentity("ade45@gmail.com");
            meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
            meedleUser = userIdentityOutputPort.save(meedleUser);
            employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
            employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
            organizationIdentity = TestData.createOrganizationTestData("Organization test1", "RC3456891", List.of(employeeIdentity));
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            userIdentity = TestData.createTestUserIdentity("loanee@grr.la");
            userIdentity.setRole(IdentityRole.LOANEE);
            userIdentity = userIdentityOutputPort.save(userIdentity);
            program = TestData.createProgramTestData("Software engineer");
            program.setCreatedBy(meedleUser.getId());
            organizationIdentity.setServiceOfferings(List.of(ServiceOffering.builder().name(TRAINING.name()).build()));
            program.setOrganizationIdentity(organizationIdentity);
            program = programOutputPort.saveProgram(program);
            programLoanDetail = TestData.buildProgramLoanDetail(program);
        } catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
    }


    @Test
    void saveNullProgramLoanDetail(){
        assertThrows(MeedlException.class, () -> programLoanDetailOutputPort.save(null));
    }

    @Test
    void saveProgramLoanDetailWithNullProgram(){
        assertThrows(MeedlException.class, () -> programLoanDetailOutputPort.save(null));
    }

    @Order(1)
    @Test
    void saveProgramLoanDetail(){
        ProgramLoanDetail savedProgramLoanDetail = ProgramLoanDetail.builder().build();
        try {
            savedProgramLoanDetail = programLoanDetailOutputPort.save(programLoanDetail);
            loanDetailsId = savedProgramLoanDetail.getId();
        }catch (MeedlException exception){
            log.info(exception.getMessage());
        }
        assertEquals(savedProgramLoanDetail.getProgram().getId(), program.getId());
    }

    @Test
    void findProgramLoanDetailWithNullProgramId(){
        assertThrows(MeedlException.class, () -> programLoanDetailOutputPort.findByProgramId(null));
    }


    @Order(2)
    @Test
    void findProgramLoanDetail(){
        ProgramLoanDetail foundLoanDetail = ProgramLoanDetail.builder().build();
        try {
            foundLoanDetail = programLoanDetailOutputPort.findByProgramId(program.getId());
        }catch (MeedlException exception){
            log.info(exception.getMessage());
        }
        assertEquals(foundLoanDetail.getProgram().getId(), program.getId());
    }



    @AfterAll
    void cleanUp() throws MeedlException {
        programLoanDetailOutputPort.delete(loanDetailsId);
        log.info("program id = {}", program.getId());
        programOutputPort.deleteProgram(program.getId());
        log.info("org id = {}", organizationIdentity.getId());
        InstituteMetrics instituteMetrics = instituteMetricsOutputPort.findByOrganizationId(organizationIdentity.getId());
        if (ObjectUtils.isNotEmpty(instituteMetrics)){
            log.info("Metrics was found for this organization");
            instituteMetricsOutputPort.delete(instituteMetrics.getId());
        }
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        log.info("org empoyee  = {}", employeeIdentity.getId());
        organizationEmployeeIdentityOutputPort.delete(employeeIdentity.getId());
        log.info("meedl id = {}", meedleUser.getId());
        userIdentityOutputPort.deleteUserById(meedleUser.getId());
        log.info("user id = {}", userIdentity.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
    }



}
