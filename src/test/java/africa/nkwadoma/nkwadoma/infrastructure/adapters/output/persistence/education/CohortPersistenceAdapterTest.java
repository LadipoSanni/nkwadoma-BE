package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.ProgramOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import africa.nkwadoma.nkwadoma.domain.model.education.Program;
import africa.nkwadoma.nkwadoma.domain.model.education.ServiceOffering;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortPersistenceAdapterTest {

    @Autowired
    private CohortOutputPort cohortOutputPort;
    private Cohort elites;
    private Cohort xplorers;
    private Cohort cohort;
    @Autowired
    private CohortRepository cohortRepository;


    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private  OrganizationEmployeeIdentity employeeIdentity;
    private  OrganizationIdentity organizationIdentity;
    private   Program program;
    private String cohortOne;
    private String cohortTwo;



    @BeforeAll
    void setUpOrg() {
        UserIdentity userIdentity = UserIdentity.builder().firstName("Fred").role(IdentityRole.valueOf("PORTFOLIO_MANAGER")).
                lastName("Benson").email("fred@example.com").createdBy("8937-b9897g3-bv38").build();
        employeeIdentity = OrganizationEmployeeIdentity.builder()
                .middlUser(userIdentity).build();
        organizationIdentity = OrganizationIdentity.builder().email("org@example.com").
                name("My Organization").rcNumber("56767").serviceOfferings(
                        List.of(ServiceOffering.builder().industry(Industry.EDUCATION).build())).
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
        elites.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        elites.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        elites.setProgramId(program.getId());
        elites.setName("Elite");
        elites.setCreatedBy("76154431-8764-415a-8bb5-70dab8e45111");

        cohort = new Cohort();
        cohort.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        cohort.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        cohort.setProgramId(program.getId());
        cohort.setName("Elite");
        cohort.setCreatedBy("76154431-8764-415a-8bb5-70dab8e45111");

        xplorers = new Cohort();
        xplorers.setName("xplorers");
        xplorers.setStartDate(LocalDateTime.of(2024,10,18,9,43));
        xplorers.setExpectedEndDate(LocalDateTime.of(2024,11,18,9,43));
        xplorers.setProgramId(program.getId());
        xplorers.setCreatedBy("76154431-8764-415a-8bb5-70dab8e45111");
    }
    
    
    @Test
    void saveCohortWithNullCohort(){
        assertThrows(EducationException.class, ()-> cohortOutputPort.saveCohort(null));
    }
    @Test
    void saveCohortWithNullProgramId(){
        elites.setProgramId(null);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveCohortWithValidProgramId(String programId){
        elites.setProgramId(programId);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveCohortWithEmptyName(String name){
        elites.setName(name);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " ", "email@gmail.com","3gdgttebdindndd673ydieyendjdljdh"})
    void saveCohortWithInvalidCreator(String createdBy){
        //TODO validate for UUID
        elites.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }

    @Test
    void saveCohort() {
        try {
            Cohort cohort = cohortOutputPort.saveCohort(xplorers);
            assertEquals(cohort.getName(), xplorers.getName());
            cohortOne = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveCohortWithExistingCohortName() {
        assertThrows(MeedlException.class,() ->cohortOutputPort.saveCohort(cohort));
    }


    @Test
    void saveAnotherCohortInProgram() {
        try {
            Cohort cohort = cohortOutputPort.saveCohort(elites);
            assertEquals(cohort.getName(), elites.getName());
            assertEquals(ActivationStatus.ACTIVE.name(),cohort.getCohortStatus().name());
            cohortTwo = cohort.getId();
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @AfterAll
    void cleanUp() throws MeedlException {
//        programOutputPort.deleteProgram(program.getId());
        OrganizationIdentity foundOrganization = organizationIdentityOutputPort.findByEmail(organizationIdentity.getEmail());
        organizationIdentityOutputPort.delete(foundOrganization.getId());
//        cohortRepository.deleteOrganizationServiceOffering(cohortOne);
//        cohortRepository.deleteOrganizationServiceOffering(cohortTwo);
    }
}
