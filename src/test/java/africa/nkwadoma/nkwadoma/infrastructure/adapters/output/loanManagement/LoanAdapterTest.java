package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanManagement;

import africa.nkwadoma.nkwadoma.application.ports.output.education.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.*;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.*;
import africa.nkwadoma.nkwadoma.domain.model.identity.*;
import africa.nkwadoma.nkwadoma.domain.model.loan.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
import africa.nkwadoma.nkwadoma.test.data.TestData;
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
    private LoanRequestOutputPort loanRequestOutputPort;
    private Loan loan;
    private LoaneeLoanAccount loaneeLoanAccount;
    private String savedLoanId;
    private String loaneeId;
    private String loanId;
    private LoanReferral loanReferral;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanRequest loanRequest;
    private String loanReferralId;
    private String loanRequestId;
    @Autowired
    private LoaneeLoanAccountPersistenceAdapter loaneeLoanAccountOutputPort;
    private String loaneeLoanAccountId;
    private String programId;
    private OrganizationIdentity amazingGrace;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private List<LoanBreakdown> loanBreakdowns;
    private String amazingGraceId;
    private String cohortId;

    @BeforeAll
    public void setUp(){
        UserIdentity userIdentity = TestData.createTestUserIdentity("testuser@email.com");
        List<OrganizationEmployeeIdentity> employees = List.of(OrganizationEmployeeIdentity
                .builder().meedlUser(userIdentity).build());
        amazingGrace = TestData.createOrganizationTestData(
                "Amazing Grace Enterprises",
                "RC9500034",
                employees
        );
        try {
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
        } catch (MeedlException e) {
            log.error("Error deleting organization {}", e.getMessage());
        }
        try {
            userIdentity = saveUserIdentity(userIdentity);
        } catch (MeedlException e) {
            log.error("Error saving user {}", e.getMessage());
            throw new RuntimeException(e);
        }
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        Loanee loanee = TestData.createTestLoanee(userIdentity, loaneeLoanDetail);

        try {
            Program program = TestData.createProgramTestData("Data Analytics");
            program = programOutputPort.saveProgram(program);
            program.setCreatedBy(userIdentity.getId());
            assertNotNull(program);
            programId = program.getId();

            LoanBreakdown loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdowns = List.of(loanBreakdown);
            loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown
                    (loanBreakdowns);

            Cohort cohort = TestData.createCohortData
                    ("elite", programId, amazingGraceId,
                            loanBreakdowns, userIdentity.getId());
            cohort = cohortOutputPort.save(cohort);
            cohortId = cohort.getId();
        } catch (MeedlException e) {
            log.error("Error deleting test data: {}", e.getMessage());
        }
        try {
            loanee = loaneeOutputPort.save(loanee);
        } catch (MeedlException e) {
            log.error("Error saving loanee {}", e.getMessage());
            throw new RuntimeException(e);
        }
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW, loanee.getId());
        try {
            LoaneeLoanAccount foundLoaneeAccount = loaneeLoanAccountOutputPort.findByLoaneeId(loanee.getId());
            if (ObjectUtils.isEmpty(foundLoaneeAccount)) {
                loaneeLoanAccount = loaneeLoanAccountOutputPort.save(loaneeLoanAccount);
                loaneeLoanAccountId = loaneeLoanAccount.getId();
            }
            else loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(foundLoaneeAccount.getId());
        } catch (MeedlException e) {
            log.error("Error saving loanee account", e);
        }
        loanReferral = LoanReferral.builder().loanee(loanee).loanReferralStatus(LoanReferralStatus.ACCEPTED).build();
        loanRequest = LoanRequest.builder().loanAmountRequested(loanReferral.getLoanee().getLoaneeLoanDetail().getAmountRequested())
                .status(LoanRequestStatus.APPROVED).referredBy("Brown Hills Institute").loanee(loanee).createdDate(LocalDateTime.now()).
                loaneeId("88ee2dd8-df66-4f67-b718-dfd1635f8053").loanReferralId(loanReferral.getId()).cohortId("3012eabb-4cc7-4f48-bae9-04c0056518f0")
                .dateTimeApproved(LocalDateTime.now()).build();
        try {
            loanReferral = loanReferralOutputPort.save(loanReferral);
            assertNotNull(loanReferral);
            assertNotNull(loanReferral.getId());
            loanReferralId = loanReferral.getId();

            loanRequest = loanRequestOutputPort.save(loanRequest);
            log.info("Loan request saved: {}", loanRequest);
            assertNotNull(loanRequest.getId());
            loanRequestId = loanRequest.getId();
        } catch (MeedlException e) {
            log.error("Error saving loan referral and loan request", e);
        }
        loaneeId = loanee.getId();
        loan = TestData.createTestLoan(loanee);
        loanee.setUserIdentity(userIdentity);
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
        Loan loan = null;
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
    void findAllLoanByOrganizationId() {
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

        Page<Loan> loans = null;
        try {
            loans = loanOutputPort.findAllByOrganizationId
                    (amazingGraceId, 10, 0);
        } catch (MeedlException e) {
            log.error("Error finding loans by organization: {}", e.getMessage());
        }
        assertNotNull(loans);
        assertNotNull(loans.getContent());
        assertEquals(1, loans.getTotalPages());
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid.id"})
    void deleteLoanByInvalidId(String id){
        assertThrows(MeedlException.class,()->loanOutputPort.deleteById(id));
    }

//    @AfterAll
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

        if (StringUtils.isNotEmpty(loanRequestId)) {
            try {
                loanRequestOutputPort.deleteLoanRequestById(loanRequestId);
            } catch (MeedlException e) {
                log.error("Error deleting loan request: {}", e.getMessage());
            }
        }
        if (StringUtils.isNotEmpty(loanReferralId) ) {
            try {
                loanReferralOutputPort.deleteLoanReferral(loanReferral.getId());
            } catch (MeedlException e) {
                log.error("Error deleting loan referral: {}", e.getMessage());
            }
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
        if (StringUtils.isNotEmpty(loaneeId)) {
            try {
                loaneeOutputPort.deleteLoanee(loaneeId);

            } catch (MeedlException e) {
                log.error("Error deleting loanee {}", e.getMessage());
                throw new RuntimeException(e);
            }
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