package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanAdapterTest {
    @Autowired
    private LoanOutputPort loanOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoanOfferOutputPort loanOfferOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private LoanRequestOutputPort loanRequestOutputPort;
    private String loanRequestId;
    @Autowired
    private LoaneeLoanAccountOutputPort loaneeLoanAccountOutputPort;
    @Autowired
    private NextOfKinOutputPort nextOfKinOutputPort;
    private Loan loan;
    private LoaneeLoanAccount loaneeLoanAccount;
    private String savedLoanId;
    private String loaneeId;
    private String loanId;
    private LoanReferral loanReferral;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanRequest loanRequest;
    private String loanReferralId;
    private String loaneeLoanAccountId;
    private String programId;
    private OrganizationIdentity amazingGrace;
    private List<LoanBreakdown> loanBreakdowns;
    private String amazingGraceId;
    private String cohortId;
    private String loaneeUserId;
    private Loanee loanee;
    private UserIdentity savedLoaneeUser;
    private String userId;
    private String loanOfferId;
    private String organizationEmployeeIdentityId;
    private Cohort cohort;
    private Program program;
    private NextOfKin nextOfKin;
    private String nextOfKinId;
    private int pageSize = 10;
    private int pageNumber = 0;

    @BeforeAll
    public void setUp(){
        try {
            UserIdentity userIdentity = TestData.createTestUserIdentity("testuser@email.com");
            List<OrganizationEmployeeIdentity> employees = List.of(OrganizationEmployeeIdentity
                    .builder().meedlUser(userIdentity).build());
            amazingGrace = TestData.createOrganizationTestData(
                    "Amazing Grace Enterprises",
                    "RC9500034",
                    employees
            );
            Optional<OrganizationIdentity> organization =
                    organizationIdentityOutputPort.findByOrganizationId(amazingGrace.getId());
            if (organization.isPresent()) {
                organizationIdentityOutputPort.delete(organization.get().getId());
            }
            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(amazingGrace);
            log.info("Organization saved id is : {}", savedOrganization.getId());
            assertNotNull(savedOrganization);
            assertNotNull(savedOrganization.getId());
            amazingGraceId = savedOrganization.getId();
//            userIdentity = saveUserIdentity(userIdentity);

            userIdentity = userIdentityOutputPort.save(userIdentity);
            assertNotNull(userIdentity);
            userId = userIdentity.getId();

            OrganizationEmployeeIdentity organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(amazingGraceId);
            organizationEmployeeIdentity.setMeedlUser(userIdentity);
            organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);
            assertNotNull(organizationEmployeeIdentity);
            organizationEmployeeIdentityId = organizationEmployeeIdentity.getId();

            program = TestData.createProgramTestData("Data Analytics");
            program.setCreatedBy(userId);
            program.setOrganizationIdentity(savedOrganization);
            program = programOutputPort.saveProgram(program);
            assertNotNull(program);
            assertNotNull(program.getId());
            programId = program.getId();

            cohort = TestData.createCohortData
                    ("elite", programId, amazingGraceId,
                            loanBreakdowns, userId);
            cohort = cohortOutputPort.save(cohort);
            assertNotNull(cohort);
            assertNotNull(cohort.getId());
            cohortId = cohort.getId();

            LoanBreakdown loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdown.setCohort(cohort);
            loanBreakdowns = List.of(loanBreakdown);
            loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown
                    (loanBreakdowns);

            UserIdentity loaneeUser = TestData.createTestUserIdentity("qudus@example.com");
            savedLoaneeUser = userIdentityOutputPort.save(loaneeUser);
            assertNotNull(savedLoaneeUser);
            loaneeUserId = savedLoaneeUser.getId();

            loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loanee = TestData.createTestLoanee(savedLoaneeUser, loaneeLoanDetail);
            loanee.setUserIdentity(savedLoaneeUser);
            loanee.setCohortId(cohortId);
            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
            loaneeId = loanee.getId();


            nextOfKin = TestData.createNextOfKinData(userIdentity);
            NextOfKin savedNextOfKin = nextOfKinOutputPort.save(nextOfKin);
            assertNotNull(savedNextOfKin);
            nextOfKinId = savedNextOfKin.getId();

            loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW, loanee.getId());
            LoaneeLoanAccount foundLoaneeAccount = loaneeLoanAccountOutputPort.findByLoaneeId(loanee.getId());
            log.info("Found loanee account: {}", foundLoaneeAccount);
            if (ObjectUtils.isEmpty(foundLoaneeAccount)) {
                loaneeLoanAccount = loaneeLoanAccountOutputPort.save(loaneeLoanAccount);
                log.info("Saved loanee account: {}", loaneeLoanAccount);
                loaneeLoanAccountId = loaneeLoanAccount.getId();
            }
            else loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(foundLoaneeAccount.getId());

            loanReferral = LoanReferral.builder().loanee(loanee).
                    loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
            loanReferral = loanReferralOutputPort.save(loanReferral);
            assertNotNull(loanReferral);
            assertNotNull(loanReferral.getId());
            loanReferralId = loanReferral.getId();

            loanRequest = LoanRequest.builder().loanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested())
                    .status(LoanRequestStatus.APPROVED).referredBy("Amazing Grace Enterprises").loanee(loanee).createdDate(LocalDateTime.now()).
                    loaneeId(loanee.getId())
                    .dateTimeApproved(LocalDateTime.now()).build();
            loanRequest.setId(loanReferralId);
            loanRequest = loanRequestOutputPort.save(loanRequest);
            log.info("Loan request saved: {}", loanRequest);
            assertNotNull(loanRequest.getId());
            loanRequestId = loanRequest.getId();

            LoanOffer loanOffer = TestData.buildLoanOffer(loanRequest, loanee);
            loanOffer.setId(loanReferralId);
            loanOffer = loanOfferOutputPort.save(loanOffer);
            assertNotNull(loanOffer);
            loanOfferId = loanOffer.getId();
        } catch (MeedlException e) {
            log.error("Error deleting test data: {}", e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        loan = TestData.createTestLoan(loanee);
        loan.setLoanee(loanee);
        loan.setStartDate(LocalDateTime.now());
        loan.setLoanAccountId(loaneeLoanAccountId);
    }

    @Test
    @Order(1)
    void saveLoan(){
        Loan savedLoan;
        try {
            savedLoan = loanOutputPort.save(loan);
            savedLoanId = savedLoan.getId();
            log.info("Saved loan: {} ", savedLoan.getId());
            loanId = savedLoan.getId();
        } catch (MeedlException e) {
            log.error("Error saving loan {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        assertNotNull(savedLoan);
        assertNotNull(savedLoan.getId());
        assertNotNull(savedLoan.getStartDate());
        assertEquals(loaneeLoanAccountId, savedLoan.getLoanAccountId());
        assertEquals(loaneeId, savedLoan.getLoaneeId());
    }
    @Test
    void saveLoanWithNull() {
        assertThrows(MeedlException.class, ()->loanOutputPort.save(null));
    }
    @Test
    void saveLoanWithNullLoanee() {
        loan.setLoanee(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }
    @Test
    void saveLoanWithNullStartDate() {
        loan.setStartDate(null);
        assertThrows(MeedlException.class, ()->loanOutputPort.save(loan));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void findLoanByInvalidId(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.findLoanById(id));
    }
    @Test
    @Order(2)
    void findLoanById() {
        Loan loan;
        try {
            log.info("loan id before finding : {}", loanId);
            loan = loanOutputPort.findLoanById(loanId);
            log.info("loan id after finding : {}", loan);
        } catch (MeedlException e) {
            log.error("Error getting loan {}", e.getMessage());
            throw new RuntimeException(e);
        }
        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertEquals(loan.getId(), loanId);

    }

    @Test
    @Order(3)
    void viewLoanById() {
        Optional<Loan> loan = Optional.empty();
        try {
            loan = loanOutputPort.viewLoanById(loanId);
        } catch (MeedlException e) {
            log.error("Error finding loan details {}", e.getMessage());
        }

        assertNotNull(loan);
        assertTrue(loan.isPresent());
        assertNotNull(loan.get().getId());
        assertNotNull(loan.get().getLoaneeId());
        assertEquals(loan.get().getId(), loanId);
    }

    @Test
    @Order(4)
    void findAllLoanByOrganizationId() {
        Page<Loan> loans = Page.empty();
        try {
            loans = loanOutputPort.findAllByOrganizationId
                    (amazingGraceId, 10, 0);
        } catch (MeedlException e) {
            log.error("Error finding loans by organization: {}", e.getMessage());
        }

        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
        assertEquals(loans.getContent().get(0).getFirstName(), loanee.getUserIdentity().getFirstName());
        assertEquals(loans.getContent().get(0).getLastName(), loanee.getUserIdentity().getLastName());
        assertEquals(loans.getContent().get(0).getInitialDeposit(), loanee.getLoaneeLoanDetail().getInitialDeposit());
        assertEquals(loans.getContent().get(0).getAmountRequested(), loanee.getLoaneeLoanDetail().getAmountRequested());
        assertNotNull(loans.getContent().get(0).getOfferDate());
        assertNotNull(loans.getContent().get(0).getStartDate());
        assertEquals(loans.getContent().get(0).getCohortStartDate(), cohort.getStartDate());
        assertEquals(loans.getContent().get(0).getCohortName(), cohort.getName());
        assertEquals(loans.getContent().get(0).getProgramName(), program.getName());
    }
    @Test
    @Order(5)
    void findAllLoan(){
        Page<Loan> loans = Page.empty();
        try{
            loans = loanOutputPort.findAllLoan(pageSize,pageNumber);
        }catch (MeedlException e){
            log.error("Error finding loans : {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalElements());
    }

    @Test
    @Order(6)
    void setLoanStatus(){
        Loan foundLoan = new Loan();
        try {
            foundLoan = loanOutputPort.findLoanById(loanId);
            foundLoan.setLoanStatus(LoanStatus.DEFERRED);
            foundLoan = loanOutputPort.save(foundLoan);
        } catch (MeedlException e) {
            log.error("Error  : {}", e.getMessage());
        }
        assertNotNull(foundLoan);
        assertEquals(LoanStatus.DEFERRED, foundLoan.getLoanStatus());
        assertEquals(loanId, foundLoan.getId());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void deleteLoanByInvalidId(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.deleteById(id));
    }

    @AfterAll
    void tearDown() {
        deleteLoan();

        try {
            List<OrganizationServiceOffering> organizationServiceOfferings = organizationIdentityOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(amazingGraceId);
            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationIdentityOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            if (StringUtils.isNotEmpty(serviceOfferingId)) {
                organizationIdentityOutputPort.deleteServiceOffering(serviceOfferingId);
            }
            organizationIdentityOutputPort.delete(amazingGraceId);
        } catch (MeedlException e) {
            log.error("Error deleting organization", e);
        }
    }

    private void deleteLoan() {
        try {
            nextOfKinOutputPort.deleteNextOfKin(nextOfKinId);
        } catch (MeedlException e) {
            log.error("Error deleting next of kin", e);
        }

        if (StringUtils.isNotEmpty(loaneeLoanAccountId)) {
            try {
                loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(loaneeLoanAccountId);
            } catch (MeedlException e) {
                log.error("Error deleting loanee account", e);
            }
        }
        if (StringUtils.isNotEmpty(savedLoanId)) {
            try {
                loanOutputPort.deleteById(savedLoanId);
            } catch (MeedlException e) {
                log.error("Error deleting loan {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        if (StringUtils.isNotEmpty(loanReferralId) ) {
            try {
                loanReferralOutputPort.deleteLoanReferral(loanReferral.getId());
            } catch (MeedlException e) {
                log.error("Error deleting loan referral: {}", e.getMessage());
            }
        }

        if (StringUtils.isNotEmpty(loanOfferId)) {
            loanOfferOutputPort.deleteLoanOfferById(loanOfferId);
        }

        if (StringUtils.isNotEmpty(loanRequestId)) {
            try {
                loanRequestOutputPort.deleteLoanRequestById(loanRequestId);
            } catch (MeedlException e) {
                log.error("Error deleting loan request: {}", e.getMessage());
            }
        }

        if (StringUtils.isNotEmpty(loaneeId)) {
            try {
                loaneeOutputPort.deleteLoanee(loaneeId);
            } catch (MeedlException e) {
                log.error("Error deleting loanee {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
        if (StringUtils.isNotEmpty(loaneeUserId)) {
            try {
                userIdentityOutputPort.deleteUserById(loaneeUserId);
            } catch (MeedlException e) {
                log.error("Error deleting loanee {}", e.getMessage());
            }
        }

        if (StringUtils.isNotEmpty(programId)) {
            try {
                programOutputPort.deleteProgram(programId);
            } catch (MeedlException e) {
                log.error("Error deleting program: {}", e.getMessage());
            }
        }

        if (StringUtils.isNotEmpty(cohortId)) {
            try {
                cohortOutputPort.deleteCohort(cohortId);
            } catch (MeedlException e) {
                log.error("Error deleting cohort: {}", e.getMessage());
            }
        }

        if (CollectionUtils.isNotEmpty(loanBreakdowns)) {
            loanBreakdownOutputPort.deleteAll(loanBreakdowns);
        }
    }

    private UserIdentity saveUserIdentity(UserIdentity userIdentity) throws MeedlException {
        try {
            deleteLoan();
            userIdentityOutputPort.deleteUserById(userIdentity.getId());
        } catch (MeedlException e) {
            log.error("Error deleting user {}", e.getMessage());
        }
        userIdentity = userIdentityOutputPort.save(userIdentity);
        return userIdentity;
    }
}