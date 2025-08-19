package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationEmployeeIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationLoanDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationEmployeeIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationIdentity;
import africa.nkwadoma.nkwadoma.domain.model.identity.OrganizationLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class OrganizationLoanDetailPersistenceAdapterTest {


    private UserIdentity meedleUser;
    private OrganizationEmployeeIdentity employeeIdentity;
    private OrganizationIdentity organizationIdentity;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;

    private String loanDetailsId;
    @Autowired
    private OrganizationLoanDetailOutputPort organizationLoanDetailOutputPort;
    private OrganizationLoanDetail organizationLoanDetail;



    @BeforeAll
    void setUpCohortLoanee() {
        try {
            meedleUser = TestData.createTestUserIdentity("ade45@gmail.com");
            meedleUser.setRole(IdentityRole.ORGANIZATION_ADMIN);
            meedleUser = userIdentityOutputPort.save(meedleUser);
            employeeIdentity = TestData.createOrganizationEmployeeIdentityTestData(meedleUser);
            employeeIdentity = organizationEmployeeIdentityOutputPort.save(employeeIdentity);
            organizationIdentity = TestData.createOrganizationTestData("Organization test1", "RC3456891", List.of(employeeIdentity));
            organizationIdentity = organizationIdentityOutputPort.save(organizationIdentity);
            organizationLoanDetail = TestData.buildOrganizationLoanDetail(organizationIdentity);

        } catch (MeedlException exception) {
            log.info(exception.getMessage());
        }
    }


    @Test
    void saveNullOrganizationLoanDetail(){
        assertThrows(MeedlException.class, () -> organizationLoanDetailOutputPort.save(null));
    }

    @Test
    void saveOrganizationLoanDetailWithNullOrganization(){
        assertThrows(MeedlException.class, () -> organizationLoanDetailOutputPort.save(null));
    }

    @Order(1)
    @Test
    void saveOrganizationLoanDetail(){
        OrganizationLoanDetail savedOrganizationLoanDetail = OrganizationLoanDetail.builder().build();
        try {
            savedOrganizationLoanDetail = organizationLoanDetailOutputPort.save(organizationLoanDetail);
            log.info("save loan details == {}", savedOrganizationLoanDetail);
            loanDetailsId = savedOrganizationLoanDetail.getId();
        }catch (MeedlException exception){
            log.info(exception.getMessage());
        }
        assertEquals(savedOrganizationLoanDetail.getOrganization().getId(), organizationIdentity.getId());
    }

    @Test
    void findOrganizationLoanDetailWithNullProgramId(){
        assertThrows(MeedlException.class, () -> organizationLoanDetailOutputPort.findByOrganizationId(null));
    }


    @Order(2)
    @Test
    void findOrganizationLoanDetail(){
        OrganizationLoanDetail foundOganizationLoanDetail = OrganizationLoanDetail.builder().build();
        try {
            foundOganizationLoanDetail = organizationLoanDetailOutputPort.findByOrganizationId(organizationIdentity.getId());
        }catch (MeedlException exception){
            log.info(exception.getMessage());
        }
        assertEquals(foundOganizationLoanDetail.getOrganization().getId(), organizationIdentity.getId());
    }



    @AfterAll
    void cleanUp() throws MeedlException {
        organizationLoanDetailOutputPort.delete(loanDetailsId);
        log.info("org id = {}", organizationIdentity.getId());
        organizationIdentityOutputPort.delete(organizationIdentity.getId());
        log.info("org empoyee  = {}", employeeIdentity.getId());
        organizationEmployeeIdentityOutputPort.delete(employeeIdentity.getId());
        log.info("meedl id = {}", meedleUser.getId());
        userIdentityOutputPort.deleteUserById(meedleUser.getId());
    }
}
