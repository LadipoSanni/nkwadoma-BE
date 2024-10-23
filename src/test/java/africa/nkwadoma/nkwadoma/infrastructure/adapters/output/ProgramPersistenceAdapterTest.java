package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.*;
import org.hibernate.annotations.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.PORTFOLIO_MANAGER;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramPersistenceAdapterTest {
    @Autowired
    private ProgramOutputPort programOutputPort;
    private Program program;
    private OrganizationIdentity organizationIdentity;
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private UserIdentity userIdentity;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        userIdentity = new UserIdentity();
        userIdentity.setFirstName("Joel");
        userIdentity.setLastName("Jacobs");
        userIdentity.setEmail("joel@johnson.com");
        userIdentity.setId(userIdentity.getEmail());
        userIdentity.setPhoneNumber("098647748393");
        userIdentity.setEmailVerified(true);
        userIdentity.setEnabled(true);
        userIdentity.setCreatedAt(LocalDateTime.now().toString());
        userIdentity.setRole(PORTFOLIO_MANAGER);
        userIdentity.setCreatedBy("Ayo");

        OrganizationEmployeeIdentity employeeIdentity = OrganizationEmployeeIdentity.builder().middlUser(userIdentity).build();
        organizationIdentity = new OrganizationIdentity();
        organizationIdentity.setName("Amazing Grace Enterprises");
        organizationIdentity.setEmail("rachel@gmail.com");
        organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
        organizationIdentity.setRcNumber("RC345677");
//        organizationIdentity.setId(organizationIdentity.getRcNumber());
        organizationIdentity.setPhoneNumber("0907658483");
        organizationIdentity.setTin("Tin5678");
        organizationIdentity.setNumberOfPrograms(0);
        ServiceOffering serviceOffering = new ServiceOffering();
        serviceOffering.setName(ServiceOfferingType.TRAINING.name());
        serviceOffering.setIndustry(Industry.EDUCATION);
        organizationIdentity.setServiceOfferings(List.of(serviceOffering));
        organizationIdentity.setWebsiteAddress("webaddress.org");
        organizationIdentity.setOrganizationEmployees(List.of(employeeIdentity));

        program = new Program();
        program.setName("Software Engineering");
        program.setProgramDescription("A rigorous course in the art and science of software engineering");
        program.setMode(ProgramMode.FULL_TIME);
        program.setProgramStatus(ActivationStatus.ACTIVE);
        program.setDuration(2);
        program.setDeliveryType(DeliveryType.ONSITE);
        program.setCreatedBy(userIdentity.getCreatedBy());
        program.setDurationType(DurationType.MONTHS);
    }


    @Test
    @Order(1)
    void saveProgram() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            userIdentityOutputPort.save(userIdentity);
            OrganizationIdentity foundOrganization = organizationOutputPort.findById(savedOrganization.getId());
            assertNotNull(foundOrganization);
            assertNotNull(foundOrganization.getId());

            program.setOrganizationId(foundOrganization.getId());
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(program.getName(), savedProgram.getName());
            assertEquals(program.getProgramStatus(), savedProgram.getProgramStatus());
            assertEquals(program.getProgramDescription(), savedProgram.getProgramDescription());
            assertEquals(LocalDate.now(), savedProgram.getProgramStartDate());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
    }

    @Test
    void createProgramWithNullProgram(){
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram((null)));
    }

    @Test
    void saveProgramWithNonEducationIndustry() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            organization.setServiceOfferings(List.of(ServiceOffering.builder().industry(Industry.BANKING).build()));

            OrganizationIdentity savedOrganization = organizationOutputPort.save(organization);
            userIdentityOutputPort.save(userIdentity);
            assertNotNull(savedOrganization);
            assertEquals(Industry.BANKING, savedOrganization.getServiceOfferings().get(0).getIndustry());

            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            foundProgram.setOrganizationId(savedOrganization.getId());

            assertThrows(MeedlException.class, ()-> programOutputPort.saveProgram(foundProgram));
        } catch (MeedlException e) {
            log.error("Error while saving program", e);
        }
    }

    @Test
    void createProgramWithExistingName(){
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            assertNotNull(foundProgram);
            assertEquals(program.getName(), foundProgram.getName());
        } catch (MeedlException exception) {
            log.error("{} {}", exception.getClass().getName(), exception.getMessage());
        }
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"    Design Thinking", "Data Science      "})
    void createProgramWithSpacesInProgramName(String programName){
        OrganizationIdentity foundOrganization = null;
        try {
            foundOrganization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());

        } catch (MeedlException e) {
            try {
                foundOrganization = organizationOutputPort.save(organizationIdentity);
            } catch (MeedlException ex) {
                log.error("Error saving program", e);
            }
        }
        try{
            assertNotNull(foundOrganization);
            assertNotNull(foundOrganization.getId());

            program.setOrganizationId(foundOrganization.getId());
            program.setName(programName);
            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertNotNull(savedProgram.getId());
            assertEquals(programName.trim(), savedProgram.getName());
        } catch (MeedlException e) {
            log.error("Error saving program", e);
        }
    }

    @Test
    void createProgramWithNullName(){
        program.setName(null);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram((program)));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidName(String name){
        program.setName(name);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @Test
    void createProgramWithNullOrganizationId(){
        program.setOrganizationId(null);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram((program)));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidOrganizationId(String organzationId){
        program.setOrganizationId(organzationId);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @Test
    void createProgramWithNullCreatedBy() {
        program.setCreatedBy(null);
        assertThrows(MeedlException.class, () -> programOutputPort.saveProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void createProgramWithInvalidCreatedBy(String createdBy){
        program.setCreatedBy(createdBy);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Non existing created by"})
    void createProgramWithNonExistingCreatedBy(String createdBy){
        try {
            OrganizationIdentity foundOrganization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            assertEquals(foundOrganization.getOrganizationEmployees().get(0).getMiddlUser().getCreatedBy(),
                    organizationIdentity.getOrganizationEmployees().get(0).getMiddlUser().getCreatedBy()
            );
            assertTrue(foundOrganization.getOrganizationEmployees().get(0).getMiddlUser().isEnabled());
        } catch (MeedlException e) {
            log.error("", e);
        }
        program.setCreatedBy(createdBy);
        assertThrows(MeedlException.class,()-> programOutputPort.saveProgram(program));
    }

    @Test
    @Order(2)
    void findProgramByName() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            assertNotNull(foundProgram);
            assertEquals(foundProgram.getName(), program.getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findProgramByNullOrEmptyName(String name) {
        MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.findProgramByName(name));
        assertEquals(meedlException.getMessage(), MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"  My program", "My program   ", "    My program     "})
    void findProgramByNameWithSpaces(String name) {
        try {
            Program foundProgramByName = programOutputPort.findProgramByName(name);
            assertNotNull(foundProgramByName);
            assertEquals(foundProgramByName.getName(), program.getName());
        } catch (MeedlException e) {
            log.error("Error finding program by name with spaces", e);
        }
    }

    @Test
    @Order(3)
    void findProgramById() {
        try {
            Program foundProgramByName = programOutputPort.findProgramByName(program.getName());

            Program foundProgram = programOutputPort.findProgramById(foundProgramByName.getId());

            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());
        } catch (MeedlException e) {
            log.error("Error finding program by ID", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findProgramWithInvalidId(String id){
        program.setId(id);
        assertThrows(MeedlException.class,()-> programOutputPort.findProgramById(program.getId()));
    }

    @Test
    void findProgramWithNullProgramId(){
        program.setId(null);
        assertThrows(MeedlException.class,()-> programOutputPort.findProgramById((program.getId())));
    }

    @Test
    @Order(4)
    void findAllPrograms() {
        try {
            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            assertNotNull(organization);
            assertNotNull(organization.getId());

            program.setOrganizationId(organization.getId());
            Page<Program> foundPrograms = programOutputPort.findAllPrograms(program.getOrganizationId(), pageSize, pageNumber);
            List<Program> programsList = foundPrograms.toList();

            assertEquals(1, foundPrograms.getTotalElements());
            assertEquals(1, foundPrograms.getTotalPages());
            assertTrue(foundPrograms.isFirst());
            assertTrue(foundPrograms.isLast());

            assertNotNull(programsList);
            assertEquals(1, programsList.size());
            assertEquals(programsList.get(0).getName(), program.getName());
            assertEquals(programsList.get(0).getDuration(), program.getDuration());
            assertEquals(programsList.get(0).getNumberOfCohort(), program.getNumberOfCohort());
            assertEquals(programsList.get(0).getNumberOfTrainees(), program.getNumberOfTrainees());
        } catch (MeedlException e) {
            log.error("Error finding all programs", e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.SPACE, StringUtils.EMPTY, "f98hv"})
    void findAllProgramsByNullOrInvalidOrganizationId(String organizationId) {
        try {
            Page<Program> foundPrograms = programOutputPort.findAllPrograms(organizationId, pageSize, pageNumber);
            List<Program> foundProgramsList = foundPrograms.toList();
            assertTrue(foundProgramsList.isEmpty());
            assertEquals(foundProgramsList, List.of());
        } catch (MeedlException e) {
            log.error("Failed to find all programs", e);
        }
    }

    @Test
    @Order(5)
    void deleteProgram() {
        try {
            Program foundProgram = programOutputPort.findProgramByName(program.getName());
            assertNotNull(foundProgram);
            assertNotNull(foundProgram.getId());

            programOutputPort.deleteProgram(foundProgram.getId());

            MeedlException meedlException = assertThrows(MeedlException.class, () -> programOutputPort.findProgramById(program.getId()));
            assertEquals(meedlException.getMessage(), MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage());

            OrganizationIdentity organization = organizationOutputPort.findByEmail(organizationIdentity.getEmail());
            assertNotNull(organization);
            organizationOutputPort.delete(organization.getId());
            assertThrows(ResourceNotFoundException.class, ()-> organizationOutputPort.findById(organization.getId()));

        } catch (MeedlException e) {
            log.error("Error while deleting program", e);
        }
    }


}