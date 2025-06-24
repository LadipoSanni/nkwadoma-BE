package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.education.EducationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortLoaneePersistenceAdapterTest {

    @Autowired
    private CohortLoaneeOutputPort cohortLoaneeOutputPort;


    @Test
    void saveNullCohortLoanee() {
        assertThrows(EducationException.class, () -> cohortLoaneeOutputPort.save(null));
    }

}
