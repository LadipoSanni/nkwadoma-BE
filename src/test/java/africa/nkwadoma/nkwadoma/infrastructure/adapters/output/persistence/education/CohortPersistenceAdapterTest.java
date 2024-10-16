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

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CohortPersistenceAdapterTest {
    @Autowired
    private CohortOutputPort cohortOutputPort;
    private Cohort elites;

    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort ;
    private String programId;
    private UserIdentity userIdentity;
//    @BeforeAll
    void setUpOrg() {
        UserIdentity userIdentity = UserIdentity.builder().firstName("Fred").role("PORTFOLIO_MANAGER").
                lastName("Benson").email("fred@example.com").createdBy("8937-b9897g3-bv38").build();
        OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder()
                .middlUser(userIdentity).build();
        OrganizationIdentity organizationIdentity = OrganizationIdentity.builder().email("org@example.com").
                name("My Organization").industry("My industry").rcNumber("56767").serviceOffering(
                        ServiceOffering.builder().industry(Industry.EDUCATION).build()).
                phoneNumber("09084567832").organizationEmployees(List.of(employeeIdentity)).build();

        Program program = Program.builder().name("My program").
                programStatus(ActivationStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                deliveryType(DeliveryType.ONSITE).programType(ProgramType.PROFESSIONAL).
                createdAt(LocalDateTime.now()).createdBy("68379").programStartDate(LocalDate.now()).build();
        try {
            userIdentityOutputPort.save(userIdentity);
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(organizationIdentity);
            program.setOrganizationId(savedOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(program);
            programId = savedProgram.getId();
            log.info("id{} = =",programId);
            assertNotNull(savedOrganization);
            assertNotNull(savedProgram);
        } catch (MeedlException e) {
            e.printStackTrace();
        }
    }


    @BeforeEach
    public void setUp(){
        elites = new Cohort();
        elites.setProgramId("1234");
        elites.setName("Elite Nigerian Students");

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
    @ValueSource(strings= {StringUtils.EMPTY, " "})
    void saveCohortWithValidProgramId(String programId){
        elites.setProgramId(programId);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }
    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, " "})
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
    void saveCohort() throws MeedlException {
//        elites.setProgramId();
        elites.setProgramId("4ce0490b-e309-4078-999b-65d357f54119");
        elites.setCreatedBy("8937-b9897g3-bv38");
        Cohort cohort = cohortOutputPort.saveCohort(elites);
        assertEquals(cohort.getName(),elites.getName());
    }
}
