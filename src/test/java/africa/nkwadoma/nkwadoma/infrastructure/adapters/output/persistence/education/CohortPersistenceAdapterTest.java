package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import africa.nkwadoma.nkwadoma.domain.model.education.Cohort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CohortPersistenceAdapterTest {
    @Autowired
    private CohortOutputPort cohortOutputPort;
    private Cohort elites;

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
    @ValueSource(strings= {StringUtils.EMPTY, " ", "email@gmail.com"})
    void saveCohortWithInvalidCreator(String createdBy){
        //TODO validate for UUID
        elites.setCreatedBy(createdBy);
        assertThrows(MeedlException.class, ()-> cohortOutputPort.saveCohort(elites));
    }

}
