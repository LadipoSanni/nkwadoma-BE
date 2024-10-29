package africa.nkwadoma.nkwadoma.domain.service.education;


import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.service.identity.OrganizationIdentityService;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.UserEntityRepository;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
public class CohortServiceTest {


    @Autowired
    private CohortUseCase cohortUseCase;
    private Cohort elites;
    private Cohort xplorers;
    private Cohort cohort;
    private String cohortOneId;
    private String cohortTwoId;
    private String meedleUser;

    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private OrganizationIdentityService organizationIdentityService;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private Program program;
    @Autowired
    private CohortRepository cohortRepository;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;



    @BeforeAll
    void setUpOrg() {
        UserIdentity userIdentity = UserIdentity.builder()
                .firstName("Fred 2")
                .role(IdentityRole.valueOf("PORTFOLIO_MANAGER"))
                .lastName("Benson")
                .email("fred2@example.com")
                .createdBy("8937-b9897g3-bv382v").build();

        employeeIdentity = OrganizationEmployeeIdentity.builder().meedlUser(userIdentity).build();
        organizationIdentity = OrganizationIdentity.builder()
                .email("org@example.com").
                name("My Organization2").rcNumber("56767").serviceOfferings(
                        List.of(ServiceOffering.builder().industry(Industry.EDUCATION).name(ServiceOfferingType.TRAINING.name()).build())).
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity)).build();

        program = Program.builder().name("My program").
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                deliveryType(DeliveryType.ONSITE).
                createdAt(LocalDateTime.now()).programStartDate(LocalDate.now()).build();
        try {
            organizationIdentity = organizationIdentityService.inviteOrganization(organizationIdentity);
            log.info("The organization created in test : {}", organizationIdentity);
            meedleUser = organizationIdentity.getOrganizationEmployees().get(0).getMeedlUser().getId();
            program.setOrganizationId(organizationIdentity.getId());
            log.info("Program before saving {}", program);
            program.setCreatedBy(organizationIdentity.getOrganizationEmployees().get(0).getMeedlUser().getCreatedBy());
            program = programOutputPort.saveProgram(program);
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }


    @BeforeEach
    public void setUp(){
        elites = new Cohort();
        elites.setProgramId(program.getId());
        elites.setName("Elite");
        elites.setCreatedBy(meedleUser);
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));

        cohort = new Cohort();
        cohort.setProgramId(program.getId());
        cohort.setName("Elite");
        cohort.setCreatedBy(meedleUser);
        cohort.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        cohort.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setProgramId(program.getId());
        xplorers.setCreatedBy(meedleUser);
        xplorers.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        xplorers.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
    }

    @Order(1)
    @Test
    void saveCohort() {

        try {
            Cohort cohort = cohortUseCase.createCohort(elites);
            assertEquals(cohort.getName(), elites.getName());
            cohortOneId = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(2)
    @Test
    void saveCohortWithExistingCohortName() {
        assertThrows(MeedlException.class,() ->cohortUseCase.createCohort(cohort));
    }


    @Order(3)
    @Test
    void saveAnotherCohortInProgram() {
        try {
            Cohort cohort = cohortUseCase.createCohort(xplorers);
            assertEquals(cohort.getName(), xplorers.getName());
            assertEquals(ActivationStatus.ACTIVE.name(),cohort.getActivationStatus().name());
            assertEquals(CohortStatus.CURRENT.name(),cohort.getCohortStatus().name());
            cohortTwoId = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Order(4)
    @Test
    void viewCohortDetails(){
        try{
            Cohort cohort = cohortUseCase.viewCohortDetails(meedleUser,program.getId(), cohortTwoId);
            assertEquals(cohort.getName(),xplorers.getName());
            assertEquals(cohort.getCreatedBy(),meedleUser);
        }catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @Test
    void viewCohortWithNullUserId(){
        assertThrows(MeedlException.class, () -> cohortUseCase.viewCohortDetails(null,
                program.getId(),
                cohortTwoId));
    }


    @Test
    void viewCohortWithNullProgramId(){
        assertThrows(MeedlException.class, () -> cohortUseCase.viewCohortDetails(elites.getCreatedBy(),
                null,
                cohortTwoId));
    }


    @Test
    void viewCohortWithNullCohortId(){
        assertThrows(MeedlException.class, () -> cohortUseCase.viewCohortDetails(elites.getCreatedBy(),
                program.getId(),
                null));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyUserId(String userId){
        assertThrows(MeedlException.class, ()->
                cohortUseCase.viewCohortDetails(userId,
                        program.getId(),
                        cohortTwoId));
    }


    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyProgramId(String programId){
        assertThrows(MeedlException.class, ()->
                cohortUseCase.viewCohortDetails(elites.getCreatedBy(),
                        programId,
                        cohortTwoId));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void viewCohortWithEmptyCohortId(String cohortId){
        assertThrows(MeedlException.class, ()->
                cohortUseCase.viewCohortDetails(elites.getCreatedBy(),
                        program.getId(),
                        cohortId));
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        programOutputPort.deleteProgram(program.getId());
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        cohortRepository.deleteById(cohortOneId);
        cohortRepository.deleteById(cohortTwoId);
        userIdentityOutputPort.deleteUserById(meedleUser);
    }

}