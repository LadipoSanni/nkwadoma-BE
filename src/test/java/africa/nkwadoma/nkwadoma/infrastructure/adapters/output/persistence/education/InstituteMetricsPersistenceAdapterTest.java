package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;

import africa.nkwadoma.nkwadoma.application.ports.output.education.InstituteMetricsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.InstituteMetrics;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class InstituteMetricsPersistenceAdapterTest {


    private OrganizationIdentity organizationIdentity;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private InstituteMetrics instituteMetrics;
    @Autowired
    private InstituteMetricsOutputPort instituteMetricsOutputPort;

    @BeforeAll
    void setUp() throws MeedlException {
        organizationIdentity = TestData.createOrganizationTestData("Lapo","Rc432134",null);
        organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
        instituteMetrics = TestData.createInstituteMetrics(organizationIdentity);
    }


    @Test
    void saveNullInstituteMetrics(){
        assertThrows(MeedlException.class,() -> instituteMetricsOutputPort.save(null));
    }

    @Test
    void saveInstituteMetricsNullWithOrganizationIdentity(){
        instituteMetrics.setOrganization(null);
        assertThrows(MeedlException.class,()->instituteMetricsOutputPort.save(instituteMetrics));
    }

    @Order(1)
    @Test
    void saveInstituteMetrics() {
        InstituteMetrics saveInstituteMetrics = InstituteMetrics.builder().build();
        try {
            saveInstituteMetrics = instituteMetricsOutputPort.save(saveInstituteMetrics);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(saveInstituteMetrics);
        assertEquals(saveInstituteMetrics.getOrganization().getName(), organizationIdentity.getName());
    }


    @AfterAll
    void tearDown() throws MeedlException {
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        instituteMetricsOutputPort.delete(instituteMetrics.getId());
    }
}
