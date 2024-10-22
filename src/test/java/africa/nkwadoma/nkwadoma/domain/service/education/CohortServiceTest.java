package africa.nkwadoma.nkwadoma.domain.service.education;


import africa.nkwadoma.nkwadoma.application.ports.input.education.CohortUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
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
    private String cohortOne;
    private String cohortTwo;

    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    private Program program;
    @Autowired
    private CohortRepository cohortRepository;


    @BeforeAll
    void setUpOrg() {
        UserIdentity userIdentity = UserIdentity.builder().firstName("Fred").role(IdentityRole.valueOf("PORTFOLIO_MANAGER")).
                lastName("Benson").email("fred@example.com").createdBy("8937-b9897g3-bv38").build();
        employeeIdentity = OrganizationEmployeeIdentity.builder()
                .middlUser(userIdentity).build();
        organizationIdentity = OrganizationIdentity.builder().email("org@example.com").
                name("My Organization").rcNumber("56767").serviceOffering(
                        ServiceOffering.builder().industry(Industry.EDUCATION).build()).
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity)).build();

        program = Program.builder().name("My program").
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                deliveryType(DeliveryType.ONSITE).
                createdAt(LocalDateTime.now()).createdBy("68379").programStartDate(LocalDate.now()).build();
        try {
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            program.setOrganizationId(organizationIdentity.getId());
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
        elites.setCreatedBy(program.getId());
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));

        cohort = new Cohort();
        cohort.setProgramId(program.getId());
        cohort.setName("Elite");
        cohort.setCreatedBy(program.getId());
        cohort.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        cohort.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setProgramId(program.getId());
        xplorers.setCreatedBy(program.getId());
        xplorers.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        xplorers.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
    }

    @Test
    void saveCohort() {
        try {
            Cohort cohort = cohortUseCase.createCohort(xplorers);
            assertEquals(cohort.getName(), xplorers.getName());
            cohortOne = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveCohortWithExistingCohortName() {
        assertThrows(MeedlException.class,() ->cohortUseCase.createCohort(cohort));
    }


    @Test
    void saveAnotherCohortInProgram() {
        try {
            Cohort cohort = cohortUseCase.createCohort(elites);
            assertEquals(cohort.getName(), elites.getName());
            cohortTwo = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        programOutputPort.deleteProgram(program.getId());
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        cohortRepository.deleteById(cohortOne);
        cohortRepository.deleteById(cohortTwo);
    }

}