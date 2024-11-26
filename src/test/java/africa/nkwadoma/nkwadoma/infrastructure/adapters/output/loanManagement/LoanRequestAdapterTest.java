package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

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

import static africa.nkwadoma.nkwadoma.domain.enums.IdentityRole.*;
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

    @BeforeAll
    void setUp() {
        try {
            userIdentity = new UserIdentity();
            userIdentity.setFirstName("Joel");
            userIdentity.setLastName("Jacobs");
            userIdentity.setEmail("joel@johnson.com");
            userIdentity.setPhoneNumber("098647748393");
            userIdentity.setId(testId);
            userIdentity.setCreatedBy(testId);
            userIdentity.setEmailVerified(true);
            userIdentity.setEnabled(true);
            userIdentity.setCreatedAt(LocalDateTime.now().toString());
            userIdentity.setRole(PORTFOLIO_MANAGER);
            organizationIdentity = new OrganizationIdentity();
            organizationIdentity.setName("Amazing Grace Enterprises");
            organizationIdentity.setEmail("rachel@gmail.com");
            organizationIdentity.setInvitedDate(LocalDateTime.now().toString());
            organizationIdentity.setRcNumber("RC345677");
            organizationIdentity.setId("e66eb97f-cf79-47b0-96fa-6a460ffa7f63");
            organizationIdentity.setPhoneNumber("0907658483");
            organizationIdentity.setTin("Tin5678");
            organizationIdentity.setNumberOfPrograms(0);
            organizationIdentity.setCreatedBy(testId);
            ServiceOffering serviceOffering = new ServiceOffering();
            serviceOffering.setName(ServiceOfferingType.TRAINING.name());
            serviceOffering.setIndustry(Industry.EDUCATION);
            organizationIdentity.setServiceOfferings(List.of(serviceOffering));
            organizationIdentity.setWebsiteAddress("webaddress.org");

            organizationIdentity.setOrganizationEmployees(List.of(OrganizationEmployeeIdentity.builder().
                    meedlUser(userIdentity).build()));
            OrganizationIdentity savedOrganization = organizationOutputPort.save(organizationIdentity);
            organizationId = savedOrganization.getId();
            joelUserId = userIdentityOutputPort.save(userIdentity).getId();
            OrganizationEmployeeIdentity employeeIdentity = organizationIdentity.getOrganizationEmployees().get(0);
            employeeIdentity.setOrganization(organizationId);
            organizationIdentity.getOrganizationEmployees().forEach(
                    organizationEmployeeIdentity -> employeeIdentityOutputPort.save(employeeIdentity));

            OrganizationIdentity foundOrganization = organizationOutputPort.findById(savedOrganization.getId());
            assertNotNull(foundOrganization);
            assertNotNull(foundOrganization.getId());

            dataAnalytics = new Program();
            dataAnalytics.setName("Data Analytics");
            dataAnalytics.setProgramDescription("A rigorous course in the art and science of Data analysis");
            dataAnalytics.setMode(ProgramMode.FULL_TIME);
            dataAnalytics.setProgramStatus(ActivationStatus.ACTIVE);
            dataAnalytics.setDuration(2);
            dataAnalytics.setDeliveryType(DeliveryType.ONSITE);
            dataAnalytics.setDurationType(DurationType.MONTHS);
            dataAnalytics.setCreatedBy(userIdentity.getCreatedBy());
            dataAnalytics.setOrganizationId(organizationId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            dataAnalyticsProgramId = savedProgram.getId();

            loanDetail = LoanDetail.builder().debtPercentage(0.34).repaymentPercentage(0.67).monthlyExpected(BigDecimal.valueOf(450))
                    .totalAmountRepaid(BigDecimal.valueOf(500)).totalInterestIncurred(BigDecimal.valueOf(600))
                    .lastMonthActual(BigDecimal.valueOf(200)).totalAmountDisbursed(BigDecimal.valueOf(50000))
                    .totalOutstanding(BigDecimal.valueOf(450)).build();

            loanDetail = loanDetailsOutputPort.saveLoanDetails(loanDetail);
            loanDetailId = loanDetail.getId();

            elites = new Cohort();
            elites.setStartDate(LocalDate.of(2024, 10, 18));
            elites.setExpectedEndDate(LocalDate.of(2024, 11, 18));
            elites.setProgramId(savedProgram.getId());
            elites.setName("Elite");
            elites.setCreatedBy(userIdentity.getCreatedBy());
            elites.setLoanBreakdowns(loanBreakdowns);
            elites.setTuitionAmount(BigDecimal.valueOf(20000));
            elites.setLoanDetail(loanDetail);
            Cohort cohort = cohortOutputPort.save(elites);
            eliteCohortId = cohort.getId();

            loanBreakdown = LoanBreakdown.builder().currency("USD").itemAmount(new BigDecimal("50000"))
                    .itemName("Loan Break").cohort(cohort).build();
            loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));

            userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").firstName("Adeshina").
                    lastName("Qudus").email("test@example.com").role(IdentityRole.LOANEE).
                    createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").alternateEmail("alt@example.org").
                    alternateContactAddress("1, Onigbongbo Street, Oshodi, Lagos").alternatePhoneNumber("08075533235").build();
            loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                    loanBreakdown(loanBreakdowns).initialDeposit(BigDecimal.valueOf(3000000.00)).build();

            loanee = Loanee.builder().userIdentity(userIdentity).
                    cohortId(eliteCohortId).createdBy(userIdentity.getCreatedBy()).
                    loaneeLoanDetail(loaneeLoanDetail).build();

            UserIdentity savedUserIdentity = userIdentityOutputPort.save(loanee.getUserIdentity());
            userId = savedUserIdentity.getId();

            loanBreakdownOutputPort.saveAllLoanBreakDown(loaneeLoanDetail.getLoanBreakdown());
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loaneeLoanDetailId = loaneeLoanDetail.getId();

            loanee.setLoaneeLoanDetail(loaneeLoanDetail);
            loanee.setUserIdentity(savedUserIdentity);
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
            loanRequest.setCohortId(foundLoanee.getCohortId());
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
    void saveLoanRequestWithNullLoanee() {
        loanRequest.setLoanee(null);
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoanAmountRequested() {
        loanRequest.setLoanAmountRequested(null);
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoanRequestStatus() {
        loanRequest.setStatus(null);
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void saveLoanRequestWithNullLoaneeLoanDetail() {
        loanRequest.getLoanee().setLoaneeLoanDetail(null);
        assertThrows(MeedlException.class, () -> loanRequestOutputPort.save(loanRequest));
    }

    @Test
    void viewAllLoanRequests() {
        try {
            LoanRequest savedLoanRequest = loanRequestOutputPort.save(loanRequest);
            assertNotNull(savedLoanRequest);
            loanRequestId = savedLoanRequest.getId();
        } catch (MeedlException e) {
            log.error("", e);
        }
        Page<LoanRequest> loanRequests = Page.empty();
        try {
            loanRequests = loanRequestOutputPort.viewAll(0, 10);
        } catch (MeedlException e) {
            log.error("", e);
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