package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.input.education.*;
import africa.nkwadoma.nkwadoma.application.ports.input.loan.*;
import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanEntity.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.test.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.data.domain.*;

import java.math.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoanRequestAdapterTest {
    private final String testId = "81d45178-9b05-4f35-8d96-5759f9fc5ea7";
    @Autowired
    private LoanRequestOutputPort loanRequestOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationOutputPort;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private LoanDetailsOutputPort loanDetailsOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort employeeIdentityOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private LoanRequestRepository loanRequestRepository;
    private NextOfKin nextOfKin;
    @Autowired
    private NextOfKinIdentityOutputPort nextOfKinIdentityOutputPort;
    @Autowired
    private LoanDetailRepository loanDetailRepository;
    private Program dataAnalytics;
    private LoanReferral loanReferral;
    private LoanRequest loanRequest;
    private LoaneeLoanDetail loaneeLoanDetail;
    private Loanee loanee;
    private Cohort elites;
    private String organizationId;
    private String dataAnalyticsProgramId;
    private String eliteCohortId;
    private String loaneeId;
    private String loaneeLoanDetailId;
    private String loanReferralId;
    private String joelUserId;
    private String userId;
    private String loanRequestId;
    private LoanDetail loanDetail;
    private LoanBreakdown loanBreakdown;
    private List<LoanBreakdown> loanBreakdowns;
    private String loanDetailId;
    private UserIdentity userIdentity;
    private OrganizationIdentity organizationIdentity;
    private String nextOfKinId;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private CohortUseCase cohortUseCase;
    @Autowired
    private LoaneeUseCase loaneeUseCase;
    @Autowired
    private LoaneeLoanBreakDownRepository loaneeLoanBreakDownRepository;
    @Autowired
    private LoanBreakdownRepository loanBreakdownRepository;
    private OrganizationIdentity amazingGrace;
    private  UserIdentity joel;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private String loaneeUserId;
    private String organizationEmployeeIdentityId;
    private String programId;
    private Cohort cohort;
    private List<LoaneeLoanBreakdown> loaneeBreakdowns;
    private String cohortId;
    private String organizationAdminId;


    @BeforeAll
    void setUp() {
        try {
//            userIdentity = new UserIdentity();
//            userIdentity.setFirstName("Joel");
//            userIdentity.setLastName("Jacobs");
//            userIdentity.setEmail("joel@johnson.com");
//            userIdentity.setPhoneNumber("098647748393");
//            userIdentity.setId(testId);
//            userIdentity.setCreatedBy(testId);
//            userIdentity.setEmailVerified(true);
//            userIdentity.setEnabled(true);
//            userIdentity.setCreatedAt(LocalDateTime.now().toString());
//            userIdentity.setRole(PORTFOLIO_MANAGER);
//            organizationIdentity = new OrganizationIdentity();
//            organizationIdentity.setName("Amazing Grace Enterprises");
//            organizationIdentity.setEmail("rachel@gmail.com");
//            organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
//            organizationIdentity.setRcNumber("RC345677");
//            organizationIdentity.setId("e66eb97f-cf79-47b0-96fa-6a460ffa7f63");
//            organizationIdentity.setPhoneNumber("0907658483");
//            organizationIdentity.setTin("Tin5678");
//            organizationIdentity.setNumberOfPrograms(0);
//            organizationIdentity.setCreatedBy(testId);
//            ServiceOffering serviceOffering = new ServiceOffering();
//            serviceOffering.setName(ServiceOfferingType.TRAINING.name());
//            serviceOffering.setIndustry(Industry.EDUCATION);
//            organizationIdentity.setServiceOfferings(List.of(serviceOffering));
//            organizationIdentity.setWebsiteAddress("webaddress.org");
//
//            organizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().
//                    meedlUser(userIdentity).build()));
//            organizationIdentity = organizationOutputPort.save(organizationIdentity);
//            organizationId = organizationIdentity.getId();
//            joelUserId = userIdentityOutputPort.save(userIdentity).getId();
//            OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
//            employeeIdentity.setOrganization(organizationId);
//            organizationIdentity.getOrganizationEmployees().forEach(
//                    organizationEmployeeIdentity -> employeeIdentityOutputPort.save(employeeIdentity));
//
//            OrganizationIdentity foundOrganization = organizationOutputPort.findById(organizationIdentity.getId());
//            assertNotNull(foundOrganization);
//            assertNotNull(foundOrganization.getId());

            joel = TestData.createTestUserIdentity("joel54@johnson.com");
            List<OrganizationEmployeeIdentity> employees = List.of(OrganizationEmployeeIdentity
                    .builder().meedlUser(joel).build());

            amazingGrace = TestData.createOrganizationTestData(
                    "Amazing Grace Enterprises",
                    "RC79500034",
                    employees
            );
            amazingGrace.setServiceOfferings(List.of(ServiceOffering.builder().
                    name(ServiceOfferingType.TRAINING.name()).
                    industry(Industry.EDUCATION).build()));

            amazingGrace = organizationIdentityOutputPort.save(amazingGrace);
            assertNotNull(amazingGrace);
            organizationId = amazingGrace.getId();

            joel = userIdentityOutputPort.save(joel);
            assertNotNull(joel);
            joelUserId = joel.getId();

            organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationId);
            organizationEmployeeIdentity.setMeedlUser(joel);
            organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);

            assertNotNull(organizationEmployeeIdentity);
            organizationEmployeeIdentityId = organizationEmployeeIdentity.getId();

            dataAnalytics = TestData.createProgramTestData("Data Analytics");
            dataAnalytics.setCreatedBy(organizationAdminId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = savedProgram.getId();

            loanDetail = LoanDetail.builder().debtPercentage(0.34).repaymentPercentage(0.67).monthlyExpected(BigDecimal.valueOf(450))
                    .totalAmountRepaid(BigDecimal.valueOf(500)).totalInterestIncurred(BigDecimal.valueOf(600))
                    .lastMonthActual(BigDecimal.valueOf(200)).totalAmountDisbursed(BigDecimal.valueOf(50000))
                    .totalOutstanding(BigDecimal.valueOf(450)).build();

            loanDetail = loanDetailsOutputPort.saveLoanDetails(loanDetail);
            loanDetailId = loanDetail.getId();

            loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdowns = List.of(loanBreakdown);
            cohort = TestData.createCohortData("Elite", dataAnalyticsProgramId, organizationId, loanBreakdowns, organizationAdminId);
            cohort = cohortUseCase.createCohort(cohort);
            eliteCohortId = cohort.getId();

            loanBreakdown = LoanBreakdown.builder().currency("USD").itemAmount(new BigDecimal("50000"))
                    .itemName("Loan Break").cohort(cohort).build();
            loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));

            userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                    lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).image("loanee-img.jpg").
                    createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").alternateEmail("alt@example.org").
                    alternateContactAddress("1, Onigbongbo Street, Oshodi, Lagos").alternatePhoneNumber("08075533235").build();
            loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                    initialDeposit(BigDecimal.valueOf(3000000.00)).build();

            loanee = Loanee.builder().userIdentity(userIdentity).
                    cohortId(eliteCohortId).createdBy(userIdentity.getCreatedBy()).
                    loaneeLoanDetail(loaneeLoanDetail).build();

            UserIdentity savedUserIdentity = userIdentityOutputPort.save(loanee.getUserIdentity());
            userId = savedUserIdentity.getId();

            loanBreakdownOutputPort.saveAllLoanBreakDown(loanBreakdowns);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loaneeLoanDetailId = loaneeLoanDetail.getId();

            loanee.setLoaneeLoanDetail(loaneeLoanDetail);
            loanee.setUserIdentity(savedUserIdentity);
            loanee.setCohortId(cohort.getId());
            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
            loaneeId = loanee.getId();

            nextOfKin = new NextOfKin();
            nextOfKin.setFirstName("Ahmad");
            nextOfKin.setLastName("Doe");
            nextOfKin.setEmail("ahmad12@gmail.com");
            nextOfKin.setPhoneNumber("0785678901");
            nextOfKin.setNextOfKinRelationship("Brother");
            nextOfKin.setContactAddress("2, Spencer Street, Yaba, Lagos");
            nextOfKin.setLoanee(loanee);
            NextOfKin savedNextOfKin = nextOfKinIdentityOutputPort.save(nextOfKin);
            assertNotNull(savedNextOfKin);
            nextOfKinId = savedNextOfKin.getId();

            loanReferral = new LoanReferral();
            loanReferral.setLoanee(loanee);
            loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
            loanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
            assertNotNull(loanReferral);
            loanReferralId = loanReferral.getId();
        } catch (MeedlException e) {
            log.error("Error saving organization", e);
        }
    }

    @BeforeEach
    void init() {
        Loanee foundLoanee = null;
        try {
            foundLoanee = loaneeOutputPort.findLoaneeById(loaneeId);
        } catch (MeedlException e) {
            log.error("", e);
        }
        if (ObjectUtils.isNotEmpty(foundLoanee)) {
            loanRequest = new LoanRequest();
            loanRequest.setStatus(LoanRequestStatus.APPROVED);
            loanRequest.setReferredBy("Brown Hills Institute");
            loanee.setLoaneeLoanDetail(loaneeLoanDetail);
            loanRequest.setLoanee(foundLoanee);
            loanRequest.setLoanReferralId(loanReferralId);
            loanRequest.setCohortId(eliteCohortId);
            loanRequest.setCreatedDate(LocalDateTime.now());
            loanRequest.setLoanAmountRequested(foundLoanee.getLoaneeLoanDetail().getAmountRequested());
        }
    }

    @AfterEach
    void tearDown() {
        if (StringUtils.isNotEmpty(loanRequestId)) {
            Optional<LoanRequestEntity> loanRequestEntity = loanRequestRepository.findById(loanRequestId);
            loanRequestEntity.ifPresent(requestEntity -> loanRequestRepository.delete(requestEntity));
        }
    }

    @Test
    void save() {
        LoanRequest savedLoanRequest = null;
        try {
            savedLoanRequest = loanRequestOutputPort.save(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(savedLoanRequest);
        assertNotNull(savedLoanRequest.getId());
        assertNotNull(savedLoanRequest.getCreatedDate());
        loanRequestId = savedLoanRequest.getId();
    }

    @Test
    void saveNullLoanRequest() {
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(null));
    }

    @Test
    void saveLoanRequestWithNullLoaneeId() {
        loanRequest.getLoanee().setId(null);
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoanAmountRequested() {
        loanRequest.setLoanAmountRequested(null);
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void viewAllLoanRequests() {
        try {
            LoanRequest savedLoanRequest = loanRequestOutputPort.save(loanRequest);
            assertNotNull(savedLoanRequest);
            loanRequestId = savedLoanRequest.getId();
        } catch (MeedlException e) {
            log.error("Error saving loan request: ", e);
        }
        Page<LoanRequest> loanRequests = Page.empty();
        try {
            loanRequests = loanRequestOutputPort.viewAll(0, 10);
        } catch (MeedlException e) {
            log.error("Error viewing all loan requests ", e);
        }
        assertNotNull(loanRequests.getContent());
        assertEquals(1, loanRequests.getTotalElements());
    }

    @ParameterizedTest
    @ValueSource(ints = -1)
    void viewAllLoanRequestsWithInvalidPageNumber(int pageNumber) {
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.viewAll(pageNumber, 10));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void viewAllLoanRequestsWithInvalidPageSize(int pageSize) {
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.viewAll(0, pageSize));
    }

    @Test
    void viewLoanRequestById() {
        LoanRequest savedLoanRequest = null;
        try {
            savedLoanRequest = loanRequestOutputPort.save(loanRequest);
        } catch (MeedlException e) {
            log.error("", e);
        }
        assertNotNull(savedLoanRequest);
        assertNotNull(savedLoanRequest.getId());
        loanRequestId = savedLoanRequest.getId();
        try {
            Optional<LoanRequest> foundLoanRequest = loanRequestOutputPort.findById(loanRequestId);

            assertFalse(foundLoanRequest.isEmpty());
            assertNotNull(foundLoanRequest.get().getId());
            assertNotNull(foundLoanRequest.get().getNextOfKin());
            assertEquals(foundLoanRequest.get().getReferredBy(), organizationIdentity.getName());
            assertEquals(foundLoanRequest.get().getProgramName(), dataAnalytics.getName());
            assertEquals(foundLoanRequest.get().getCohortName(), elites.getName());
            assertEquals(foundLoanRequest.get().getCohortStartDate(), elites.getStartDate());
            assertNotNull(foundLoanRequest.get().getLoanAmountRequested());
            assertNotNull(foundLoanRequest.get().getInitialDeposit());
            assertEquals("Adeshina", foundLoanRequest.get().getFirstName());
            assertEquals("Qudus", foundLoanRequest.get().getLastName());
            assertEquals("Ahmad", foundLoanRequest.get().getNextOfKin().getFirstName());
            assertEquals("Doe", foundLoanRequest.get().getNextOfKin().getLastName());
            assertEquals("0785678901", foundLoanRequest.get().getNextOfKin().getPhoneNumber());
            assertEquals("ahmad12@gmail.com", foundLoanRequest.get().getNextOfKin().getEmail());
            assertEquals("Brother", foundLoanRequest.get().getNextOfKin().getNextOfKinRelationship());
            assertEquals("2, Spencer Street, Yaba, Lagos", foundLoanRequest.get().getNextOfKin().getContactAddress());
        } catch (MeedlException e) {
            log.error("", e);
        }
    }

    @Test
    void viewLoanRequestByIdWithNullId() {
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.findById(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"36797387091", "foireui"})
    void viewLoanRequestByNonUUID(String id) {
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.findById(id));
    }

    @AfterAll
    void cleanUp() {
        try {
            nextOfKinIdentityOutputPort.deleteNextOfKin(nextOfKinId);
            loanReferralOutputPort.deleteLoanReferral(loanReferralId);
            loaneeOutputPort.deleteLoanee(loaneeId);
            userIdentityOutputPort.deleteUserById(userId);
            userIdentityOutputPort.deleteUserById(joelUserId);
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);

            cohortOutputPort.deleteCohort(eliteCohortId);
            programOutputPort.deleteProgram(dataAnalyticsProgramId);

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(organizationId);

            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationOutputPort.deleteServiceOffering(serviceOfferingId);
            organizationOutputPort.delete(organizationId);

            loanDetailRepository.deleteById(loanDetailId);
            loanBreakdownOutputPort.deleteAll(loanBreakdowns);
        } catch (MeedlException e) {
            log.error("Exception occurred: ", e);
        }
    }
}