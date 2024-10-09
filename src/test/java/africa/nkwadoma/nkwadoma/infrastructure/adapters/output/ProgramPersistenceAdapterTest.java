package africa.nkwadoma.nkwadoma.infrastructure.adapters.output;

import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.mapper.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgramPersistenceAdapterTest {
    @InjectMocks
    private ProgramPersistenceAdapter programPersistenceAdapter;
    @Mock
    private ProgramRepository programRepository;
    @Mock
    private ProgramMapper programMapper;
    private Program program;

    @BeforeEach
    void setUp() {
        OrganizationEntity organizationEntity = OrganizationEntity.builder().id("9bb328d3-2bf4-4ad1-95d0-818a72734d00").build();
        program = Program.builder().name("My program").
                programStatus(ProgramStatus.ACTIVE).programDescription("Program description").
                mode(ProgramMode.FULL_TIME).duration(2).durationType(DurationType.YEARS).
                organizationId(organizationEntity.getId()).build();
    }

    @Test
    void createProgram() {
        when(programRepository.findByName(program.getName())).
                thenReturn(ProgramEntity.builder().name(program.getName()).build());
        Program foundProgram = null;
        try {
            foundProgram = programPersistenceAdapter.saveProgram(program);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            e.printStackTrace();
        }

        assertNotNull(foundProgram);
        assertEquals(program.getName(), foundProgram.getName());
    }

    @Test
    @Disabled
    void findProgramByName() {
        when(programRepository.findByName(program.getName())).
                thenReturn(ProgramEntity.builder().name(program.getName()).build());
        Program foundProgram = programPersistenceAdapter.findProgram(program);

        assertNotNull(foundProgram);
        assertEquals(program.getName(), foundProgram.getName());
    }
  
}