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
import africa.nkwadoma.nkwadoma.test.data.*;
import lombok.extern.slf4j.*;
import org.apache.commons.lang3.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;

import java.math.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest
class LoanReferralAdapterTest {
    @Autowired
    private LoanReferralOutputPort loanReferralOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private ProgramOutputPort programOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private OrganizationEmployeeIdentityOutputPort organizationEmployeeIdentityOutputPort;
    @Autowired
    private CohortOutputPort cohortOutputPort;
    private OrganizationIdentity amazingGrace;
    private  UserIdentity joel;
    private OrganizationEmployeeIdentity organizationEmployeeIdentity;
    private String organizationId;
    private OrganizationEmployeeIdentity savedEmployeeIdentity;
    private String loaneeUserId;
    private String organizationEmployeeIdentityId;
    private LoanReferral loanReferral;
    private Loanee loanee;
    private String loaneeId;
    private UserIdentity userIdentity;
    private String loaneeLoanDetailId;
    private String loanReferralId;
    private Program dataAnalytics;
    private String programId;
    private LoanBreakdown loanBreakdown;
    private Cohort cohort;
    private List<LoanBreakdown> loanBreakdowns;
    private String cohortId;
    private String organizationAdminId;

    @BeforeAll
    void setUp() {
        try {
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

            OrganizationIdentity savedOrganization = organizationIdentityOutputPort.save(amazingGrace);
            assertNotNull(savedOrganization);
            organizationId = savedOrganization.getId();

            joel = userIdentityOutputPort.save(joel);
            assertNotNull(joel);
            organizationAdminId = joel.getId();

            organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationId);
            organizationEmployeeIdentity.setMeedlUser(joel);
            savedEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);

            assertNotNull(savedEmployeeIdentity);
            organizationEmployeeIdentityId = savedEmployeeIdentity.getId();

            dataAnalytics = TestData.createProgramTestData("Data Analytics");
            dataAnalytics.setCreatedBy(organizationAdminId);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            programId = savedProgram.getId();

            cohort = TestData.createCohortData("Elite", programId, organizationId, loanBreakdowns, organizationAdminId);
            cohort = cohortOutputPort.save(cohort);
            cohortId = cohort.getId();

            loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdown.setCohort(cohort);
            loanBreakdowns = loanBreakdownOutputPort.saveAllLoanBreakDown(List.of(loanBreakdown));

            userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").
                    firstName("Adeshina").lastName("Qudus").email("qudus@example.com").image("loanee-img.png").
                    role(IdentityRole.LOANEE).createdBy("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").build();
            UserIdentity savedUserIdentity = userIdentityOutputPort.save(userIdentity);
            loaneeUserId = savedUserIdentity.getId();

            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(9000000.00)).
                    initialDeposit(BigDecimal.valueOf(3000000.00)).build();

            LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            loaneeLoanDetailId = savedLoaneeLoanDetail.getId();

            loanee = Loanee.builder().userIdentity(userIdentity).createdBy(organizationAdminId).
                    loaneeLoanDetail(loaneeLoanDetail).cohortId(cohortId).loaneeLoanDetail(savedLoaneeLoanDetail).build();
            loanee = loaneeOutputPort.save(loanee);
            assertNotNull(loanee);
            loaneeId = loanee.getId();

            loanReferral = new LoanReferral();
            loanReferral.setLoanee(loanee);
            loanReferral.setLoanReferralStatus(LoanReferralStatus.ACCEPTED);
            LoanReferral savedLoanReferral = loanReferralOutputPort.saveLoanReferral(loanReferral);
            assertNotNull(savedLoanReferral);
            loanReferralId = savedLoanReferral.getId();
        } catch (MeedlException e) {
            log.error("", e);
        }
    }


    @Test
    void viewLoanReferral() {
        try {
            Optional<LoanReferral> referral = loanReferralOutputPort.findLoanReferralById(loanReferralId);

            assertNotNull(referral);
            assertFalse(referral.isEmpty());
            assertNotNull(referral.get().getId());
            assertEquals("Adeshina", referral.get().getLoanee().getUserIdentity().getFirstName());
            assertEquals("Qudus", referral.get().getLoanee().getUserIdentity().getLastName());
            assertEquals("Elite", referral.get().getCohortName());
            assertEquals(cohort.getStartDate(), referral.get().getCohortStartDate());
            assertEquals("Data Analytics", referral.get().getProgramName());
            assertEquals("loanee-img.png", referral.get().getLoaneeImage());
            assertNotNull(referral.get().getTuitionAmount());
            assertNotNull(referral.get().getInitialDeposit());
            assertNotNull(referral.get().getLoanAmountRequested());
        } catch (MeedlException e) {
            log.error("Error getting loan referral", e);
        }
    }

    @Test
    void viewLoanReferralWithTrailingAndLeadingSpaces() {
        assertThrows(MeedlException.class, ()->
                loanReferralOutputPort.findLoanReferralById(loanReferralId.concat(StringUtils.SPACE)));
    }

    @Test
    void viewLoanReferralWithNullId() {
        assertThrows(MeedlException.class, ()->loanReferralOutputPort.findLoanReferralById(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void viewLoanReferralByIdWithSpaces(String loanReferralId) {
        assertThrows(MeedlException.class, ()->loanReferralOutputPort.findLoanReferralById(loanReferralId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid id", "74567"})
    void viewLoanReferralByNonUUID(String loanReferralId) {
        assertThrows(MeedlException.class, ()->loanReferralOutputPort.findLoanReferralById(loanReferralId));
    }

    @AfterAll
    void tearDown() {
        try {
            loanReferralOutputPort.deleteLoanReferral(loanReferralId);
            loaneeOutputPort.deleteLoanee(loaneeId);
            userIdentityOutputPort.deleteUserById(loaneeUserId);
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);
            loanBreakdownOutputPort.deleteAll(loanBreakdowns);
            cohortOutputPort.deleteCohort(cohortId);
            programOutputPort.deleteProgram(programId);
            organizationEmployeeIdentityOutputPort.delete(organizationEmployeeIdentityId);

            List<OrganizationServiceOffering> organizationServiceOfferings = organizationIdentityOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(organizationId);
            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationIdentityOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationIdentityOutputPort.deleteServiceOffering(serviceOfferingId);
            organizationIdentityOutputPort.delete(organizationId);
        } catch (MeedlException e) {
            log.error("Exception occurred: ", e);
        }
    }
}
 