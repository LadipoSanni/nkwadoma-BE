package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProgramPersistenceAdapterTest {
    @Autowired
    private ProgramOutputPort programOutputPort;
    private Program program;
    private OrganizationEntity organizationEntity;

    @BeforeEach
    void setUp() {
        organizationEntity = OrganizationEntity.builder().id("9bb328d3-2bf4-4ad1-95d0-818a72734d00").build();
        program = Program.builder().name("My program").
                programStatus(ProgramStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).deliveryType(DeliveryType.ONSITE).
                programType(ProgramType.PROFESSIONAL).createdAt(LocalDateTime.now()).createdBy("68379").programStartDate(LocalDate.now()).
                organizationId(organizationEntity.getId()).build();
    }

    @Test
    void saveProgram() {
        try {
            assertThrows(ResourceNotFoundException.class,
                    ()->programOutputPort.findProgramByName(program.getName()));

            Program savedProgram = programOutputPort.saveProgram(program);

            assertNotNull(savedProgram);
            assertEquals(program.getId(), savedProgram.getId());
            assertEquals(program.getName(), savedProgram.getName());
            assertEquals(program.getProgramStatus(), savedProgram.getProgramStatus());
            assertEquals(program.getProgramDescription(), savedProgram.getProgramDescription());
            assertEquals(program.getProgramType(), savedProgram.getProgramType());
            assertEquals(program.getProgramStartDate(), savedProgram.getProgramStartDate());
        } catch (MeedlException e) {
            log.info("{}", e.getMessage());
        }
    }

    @Test
    void findProgramByName() {
        try {
            Program savedProgram = programOutputPort.saveProgram(program);
            Program foundProgram = programOutputPort.findProgramByName(program.getName());

            assertNotNull(foundProgram);
            assertEquals(savedProgram, foundProgram);
        } catch (MeedlException e) {
            log.info("{}", e.getMessage());
        }

    }
  
}