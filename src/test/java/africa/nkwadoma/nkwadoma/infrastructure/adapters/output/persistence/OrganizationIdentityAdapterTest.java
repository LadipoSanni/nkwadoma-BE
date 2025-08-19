package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanType;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.organization.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class OrganizationIdentityAdapterTest {
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    @Autowired
    private LoanMetricsOutputPort loanMetricsOutputPort;
    @Autowired
    private OrganizationEntityRepository organizationEntityRepository;
    private OrganizationIdentity amazingGrace;
    private String amazingGraceId;
    private String loanMetricsId;
    private UserIdentity joel;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeEach
    void setUp() {
        joel = TestData.createTestUserIdentity("joel@johnson.com");

        OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
        organizationEmployeeIdentity.setMeedlUser(joel);
        List<OrganizationEmployeeIdentity> userIdentities = List.of(organizationEmployeeIdentity);

        amazingGrace = TestData.createOrganizationTestData("Amazing Grace Enterprises O'Neill", "RC8787767", userIdentities);
        try {
            Optional<OrganizationEntity> organization = organizationEntityRepository.findById(amazingGrace.getId());
            if (organization.isPresent()) {
                organizationOutputPort.delete(organization.get().getId());
                log.info("Successfully deleted existing organization with similar details before starting test");
            }
        } catch (MeedlException e) {
            log.error("Failed to delete organization with id : {} , because {} ",amazingGrace.getId(), e.getMessage());
        }
    }

    @AfterEach
    void clear() {
        if (StringUtils.isNotEmpty(amazingGraceId)) {
            try {
                List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                        findOrganizationServiceOfferingsByOrganizationId(amazingGraceId);
                String serviceOfferingId = null;
                for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                    serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                    organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
                }
                if (StringUtils.isNotEmpty(serviceOfferingId)) {
                    organizationOutputPort.deleteServiceOffering(serviceOfferingId);
                }
            } catch (MeedlException e) {
                log.error("Error deleting service offerings: {}", e.getMessage());
            }
        }
        if (StringUtils.isNotEmpty(loanMetricsId)) {
            try {
                loanMetricsOutputPort.delete(loanMetricsId);
            } catch (MeedlException e) {
                log.error("Error deleting loan metrics with id: {} {} ",loanMetricsId, e.getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void saveOrganization() {
        try {
            assertThrows(MeedlException.class, () -> organizationOutputPort.findById(amazingGrace.getId()));

            OrganizationIdentity savedOrganization = organizationOutputPort.save(amazingGrace);
            log.info("Organization saved id is : {}", savedOrganization.getId());
            assertNotNull(savedOrganization);
            amazingGraceId = savedOrganization.getId();
            log.info("Organization saved successfully {}", savedOrganization);

            assertEquals(amazingGrace.getName(), savedOrganization.getName());
            assertNotNull(savedOrganization.getServiceOfferings());
            assertNotNull(savedOrganization.getServiceOfferings().get(0));
            assertEquals(amazingGrace.getServiceOfferings().get(0).getIndustry(), savedOrganization.getServiceOfferings().get(0).getIndustry());
        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @Test
    void saveOrganizationWithNullOrganizationIdentity() {
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(null));
    }

    @Test
    void saveOrganizationWithNullEmail() {
        amazingGrace.setEmail(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyEmail() {
        amazingGrace.setEmail(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullRcNumber() {
        amazingGrace.setRcNumber(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyRcNumber() {
        amazingGrace.setRcNumber(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithInvalidEmailFormat() {
        amazingGrace.setEmail("invalid");
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullIndustry() {
        amazingGrace.getServiceOfferings().get(0).setIndustry(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullServiceOffering() {
        amazingGrace.setServiceOfferings(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyName() {
        amazingGrace.setName(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullName() {
        amazingGrace.setName(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyPhoneNumber() {
        amazingGrace.setPhoneNumber(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullPhoneNumber() {
        amazingGrace.setPhoneNumber(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithNullAdmin() {
        amazingGrace.setOrganizationEmployees(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithEmptyAdmin() {
        amazingGrace.setOrganizationEmployees(Collections.emptyList());
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @Test
    void saveOrganizationWithIncompleteAdminField() {
        joel.setEmail(null);
        joel.setPhoneNumber(null);
        joel.setFirstName(null);
        joel.setLastName(null);
        joel.setCreatedBy(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.save(amazingGrace));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void searchOrganizationWithInvalidName(String name) {
        Page<OrganizationIdentity> organizationIdentities = Page.empty();
        try{
         organizationIdentities = organizationOutputPort.findByName(name,ActivationStatus.ACTIVE,
                pageSize,pageNumber);
        }catch (MeedlException e){
            log.info("{} {}", e.getClass().getName(), e.getMessage());
        }
        assertTrue(organizationIdentities.isEmpty());
    }

    @Test
    @Order(2)
    void searchOrganizationByValidName() {
        Page<OrganizationIdentity> organizationIdentities = null;
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(amazingGrace);
            log.info("Saved Organization ID : {}", savedOrganization.getId());
            assertNotNull(savedOrganization);
            amazingGraceId = savedOrganization.getId();
            organizationIdentities = organizationOutputPort.findByName("a",ActivationStatus.INVITED,
                    pageSize,pageNumber );
        } catch (MeedlException e) {
            log.error("{}", e.getMessage());
        }
        assertNotNull(organizationIdentities);
        assertFalse(organizationIdentities.isEmpty());
        log.info("{}", organizationIdentities);
    }

    @Test
    void findOrganization() {
        try {
            OrganizationIdentity organizationIdentity = organizationOutputPort.findById(amazingGrace.getId());
            assertNotNull(organizationIdentity);
            assertEquals(organizationIdentity.getName(), amazingGrace.getName());
        } catch (MeedlException meedlException) {
            log.info("{}", meedlException.getMessage());
        }
    }

    @Test
    void findOrganizationServiceOfferings() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);
            amazingGraceId = savedOrganization.getId();
            assertNotNull(savedOrganization.getServiceOfferings());

            List<OrganizationServiceOffering> organizationServiceOfferings =
                    organizationOutputPort.findOrganizationServiceOfferingsByOrganizationId(amazingGrace.getId());

            assertNotNull(organizationServiceOfferings);
            assertEquals(organizationServiceOfferings.get(0).getOrganizationId(), amazingGrace.getId());
            assertNotNull(organizationServiceOfferings.get(0).getId());
            assertNotNull(organizationServiceOfferings.get(0).getServiceOffering());
        } catch (MeedlException meedlException) {
            log.info("{}", meedlException.getMessage());
        }
    }

    @Test
    void findServiceOfferings() {
        try {
            OrganizationIdentity savedOrganization = organizationOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);
            amazingGraceId = savedOrganization.getId();
            assertNotNull(savedOrganization.getServiceOfferings());

            List<ServiceOffering> serviceOfferings =
                    organizationOutputPort.getServiceOfferings(amazingGrace.getId());

            assertNotNull(serviceOfferings);
            assertNotNull(serviceOfferings.stream().map(ServiceOffering::getId));
            assertEquals(1, serviceOfferings.size());
        } catch (MeedlException meedlException) {
            log.info("{}", meedlException.getMessage());
        }
    }

    @Test
    @Order(3)
    void viewAllOrganization() {
        try {
            Page<OrganizationIdentity> foundOrganizationIdentities = organizationOutputPort.viewAllOrganization(amazingGrace);
            assertNotNull(foundOrganizationIdentities);
            List<OrganizationIdentity> organizationIdentityList = foundOrganizationIdentities.toList();
            int listSize = organizationIdentityList.size();
            log.info("{}", organizationIdentityList.size());
            log.info("{}", organizationIdentityList);

            assertEquals(listSize, foundOrganizationIdentities.getTotalElements());
            assertTrue(foundOrganizationIdentities.isFirst());
            assertTrue(foundOrganizationIdentities.isLast());

            assertNotNull(organizationIdentityList);
            assertFalse(organizationIdentityList.isEmpty());
            assertTrue(listSize > BigInteger.ZERO.intValue());
        } catch (MeedlException meedlException) {
            log.info("{}", meedlException.getMessage());
        }
    }

    @Test
    @Order(4)
    void viewAllOrganizationWithStatus() {
        Page<OrganizationIdentity> foundOrganizationIdentities = null;
        try {
            log.info("found organization {}",organizationOutputPort.findByOrganizationId(amazingGrace.getId()));
            amazingGrace.setPageSize(1);
            amazingGrace.setPageNumber(0);
            foundOrganizationIdentities = organizationOutputPort.viewAllOrganizationByStatus(amazingGrace, List.of(ActivationStatus.INVITED.name()));
            assertNotNull(foundOrganizationIdentities);
            List<OrganizationIdentity> organizationIdentityList = foundOrganizationIdentities.toList();
            int listSize = organizationIdentityList.size();
            log.info("{}", organizationIdentityList.size());
            log.info("{}", organizationIdentityList);
            assertNotNull(organizationIdentityList);
        } catch (MeedlException meedlException) {
            log.info("{}", meedlException.getMessage());
        }
//        assertEquals(ActivationStatus.INVITED, foundOrganizationIdentities.get().toList().get(0).getActivationStatus());
    }

    @Test
    @Order(4)
    void viewAllOrganizationWithStatusThrowsExceptionWithNullValue() {
        assertThrows(MeedlException.class, () -> organizationOutputPort.viewAllOrganizationByStatus(amazingGrace, null));
    }

    @Test
    void findAllOrganizationWithLoanRequests() throws MeedlException {
        try {
            amazingGrace = organizationOutputPort.save(amazingGrace);
            assertNotNull(amazingGrace);
            amazingGraceId = amazingGrace.getId();
            LoanMetrics loanMetrics = LoanMetrics.builder().organizationId(amazingGraceId)
                    .loanRequestCount(1).build();
            loanMetrics = loanMetricsOutputPort.save(loanMetrics);
            assertNotNull(loanMetrics);
            assertNotNull(loanMetrics.getId());
            loanMetricsId = loanMetrics.getId();
        } catch (MeedlException e) {
            log.error("Exception occurred saving loan metrics {}", e.getMessage());
        }
        Page<OrganizationIdentity> organizationIdentities = organizationOutputPort.findAllWithLoanMetrics(LoanType.LOAN_REQUEST,pageSize,pageNumber);
        assertNotNull(organizationIdentities);
        assertEquals(organizationIdentities.getContent().get(0).getName(), amazingGrace.getName());
        assertEquals(organizationIdentities.getContent().get(0).getLogoImage(), amazingGrace.getLogoImage());
        assertEquals(1, organizationIdentities.stream().mapToInt(OrganizationIdentity::getLoanRequestCount).sum());
    }

    @Test
    void viewAllOrganizationWithNull() {
        assertThrows(MeedlException.class, () -> organizationOutputPort.viewAllOrganization(null));

    }

    @Test
    void findNonExistingOrganization() {
        amazingGrace.setId("12345RC");
        assertThrows(MeedlException.class, () -> organizationOutputPort.findById(amazingGrace.getId()));
    }

    @Test
    void findNullOrganization() {
        amazingGrace.setId(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.findById(amazingGrace.getId()));
    }

    @Test
    void findEmptyOrganization() {
        amazingGrace.setId(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> organizationOutputPort.findById(amazingGrace.getId()));
    }

    @Test
    void deleteOrganization() {
        try {
            OrganizationIdentity foundUser = organizationOutputPort.findById(amazingGrace.getId());
            assertEquals(foundUser.getTin(), amazingGrace.getTin());
            organizationOutputPort.delete(amazingGrace.getId());
            assertThrows(ResourceNotFoundException.class, () -> organizationOutputPort.findById(amazingGrace.getId()));
        } catch (MeedlException e) {
            log.info("{} {}", e.getClass().getName(), e.getMessage());
        }
    }

    @Test
    void deleteWithNonExistingOrganizationId() {
        amazingGrace.setId("non existing101");
        assertThrows(MeedlException.class, () -> organizationOutputPort.delete(amazingGrace.getId()));
    }

    @Test
    void deleteWithNullOrganizationId() {
        amazingGrace.setId(null);
        assertThrows(MeedlException.class, () -> organizationOutputPort.delete(amazingGrace.getId()));
    }

    @Test
    void deleteWithEmptyOrganizationId() {
        amazingGrace.setId(StringUtils.EMPTY);
        assertThrows(MeedlException.class, () -> organizationOutputPort.delete(amazingGrace.getId()));
    }

    @Test
    void updateOrganization() {
        try {
            OrganizationIdentity existingUser = organizationOutputPort.findById(amazingGrace.getId());
            assertNotNull(existingUser);
            assertEquals(existingUser.getPhoneNumber(), amazingGrace.getPhoneNumber());
            assertNotNull(existingUser.getServiceOfferings());
            assertFalse(existingUser.getServiceOfferings().isEmpty());
            assertNotNull(existingUser.getServiceOfferings().get(0).getIndustry());

            existingUser.setName("Felicia");
            OrganizationIdentity updatedUser = organizationOutputPort.save(existingUser);

            OrganizationIdentity findUpdatedUser = organizationOutputPort.findById(updatedUser.getId());

            assertNotEquals(findUpdatedUser.getName(), amazingGrace.getName());

        } catch (MeedlException exception) {
            log.info("{} {}", exception.getClass().getName(), exception.getMessage());
        }
    }

    @AfterAll
    void tearDown() {
        if (StringUtils.isNotEmpty(amazingGraceId)) {
            try {
                List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                        findOrganizationServiceOfferingsByOrganizationId(amazingGraceId);
                String serviceOfferingId = null;
                for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                    serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                    organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
                }
                if (StringUtils.isNotEmpty(serviceOfferingId)) {
                    organizationOutputPort.deleteServiceOffering(serviceOfferingId);
                }
                organizationOutputPort.delete(amazingGraceId);
            } catch (MeedlException e) {
                log.error("Error deleting organization: {}", e.getMessage());
            }
        }
    }
}