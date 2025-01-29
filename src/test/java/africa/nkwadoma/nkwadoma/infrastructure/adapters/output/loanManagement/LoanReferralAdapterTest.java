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
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.*;
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
    private LoanMetricsOutputPort loanMetricsOutputPort;
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
    private String organizationId;
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
    private List<LoaneeLoanBreakdown> loaneeBreakdowns;
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
                    "RC7950005",
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
            organizationAdminId = joel.getId();

            organizationEmployeeIdentity = new OrganizationEmployeeIdentity();
            organizationEmployeeIdentity.setOrganization(organizationId);
            organizationEmployeeIdentity.setMeedlUser(joel);
            organizationEmployeeIdentity.setStatus(ActivationStatus.INVITED);
            organizationEmployeeIdentity = organizationEmployeeIdentityOutputPort.
                    save(organizationEmployeeIdentity);

            assertNotNull(organizationEmployeeIdentity);
            organizationEmployeeIdentityId = organizationEmployeeIdentity.getId();

            dataAnalytics = TestData.createProgramTestData("Data Analytics test");
            dataAnalytics.setCreatedBy(organizationAdminId);
            List<Program> foundPrograms = programOutputPort.findProgramByName(dataAnalytics.getName(), amazingGrace.getId());
            log.info("Found programs is:: {}",foundPrograms.isEmpty());
            if (!foundPrograms.isEmpty()){
                Optional<Program> optionalProgram = foundPrograms.stream()
                        .filter(program -> program.getName().equals(dataAnalytics.getName()))
                        .findFirst();
                if (optionalProgram.isPresent()){
                    Program foundProgram = optionalProgram.get();
                    programOutputPort.deleteProgram(foundProgram.getId());
                }
            }
            dataAnalytics.setOrganizationIdentity(amazingGrace);
            Program savedProgram = programOutputPort.saveProgram(dataAnalytics);
            programId = savedProgram.getId();

            loanBreakdown = TestData.createLoanBreakDown();
            loanBreakdowns = List.of(loanBreakdown);
            cohort = TestData.createCohortData("Elite", programId, organizationId, loanBreakdowns, organizationAdminId);
            cohort = cohortUseCase.createCohort(cohort);
            cohortId = cohort.getId();

            loanBreakdowns = loanBreakdownOutputPort.findAllByCohortId(cohortId);
            LoanBreakdown cohortTuitionBreakdown = loanBreakdowns.get(0);

            userIdentity = UserIdentity.builder().id("96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f").
                    firstName("Adeshina").lastName("Qudus").email("qudus@example.com").image("loanee-img.png").
                    role(IdentityRole.LOANEE).createdBy(organizationAdminId).build();
            Optional<UserIdentity> foundUser = identityManagerOutputPort.getUserByEmail(userIdentity.getEmail());
            if (foundUser.isPresent()) {
                identityManagerOutputPort.deleteUser(foundUser.get());
            }
            LoaneeLoanDetail loaneeLoanDetail = LoaneeLoanDetail.builder().
                    amountRequested(BigDecimal.valueOf(30000.00)).
                    initialDeposit(BigDecimal.valueOf(10000.00)).build();

            LoaneeLoanBreakdown loaneeLoanBreakdown = LoaneeLoanBreakdown.builder().
                    loaneeLoanBreakdownId(cohortTuitionBreakdown.getLoanBreakdownId()).
                    itemAmount(cohortTuitionBreakdown.getItemAmount()).
                    itemName(cohortTuitionBreakdown.getItemName()).build();
            loaneeBreakdowns = List.of(loaneeLoanBreakdown);
            loanee = Loanee.builder().userIdentity(userIdentity).
                    loanBreakdowns(loaneeBreakdowns).
                    cohortId(cohortId).loaneeLoanDetail(loaneeLoanDetail).build();
            loanee = loaneeUseCase.addLoaneeToCohort(loanee);
            assertNotNull(loanee);
            loaneeId = loanee.getId();
            loaneeUserId = loanee.getUserIdentity().getId();
            loaneeLoanDetailId = loanee.getLoaneeLoanDetail().getId();


            loanReferral = loaneeUseCase.referLoanee(loaneeId);
            assertNotNull(loanReferral);
            log.info("Loan referral ====> {}", loanReferral);
            loanReferralId = loanReferral.getId();
        } catch (MeedlException e) {
            log.error("Error creating set up test data", e);
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
            assertEquals(userIdentity.getAlternatePhoneNumber(),
                    referral.get().getLoanee().getUserIdentity().getAlternatePhoneNumber());
            assertEquals(userIdentity.getAlternateEmail(),
                    referral.get().getLoanee().getUserIdentity().getAlternateEmail());
            assertEquals(userIdentity.getAlternateContactAddress(),
                    referral.get().getLoanee().getUserIdentity().getAlternateContactAddress());
            assertEquals("Elite", referral.get().getCohortName());
            assertEquals(cohort.getStartDate(), referral.get().getCohortStartDate());
            assertEquals("Data Analytics", referral.get().getProgramName());
            assertEquals("loanee-img.png", referral.get().getLoaneeImage());
            assertNotNull(referral.get().getTuitionAmount());
            assertNotNull(referral.get().getInitialDeposit());
            assertNotNull(referral.get().getLoanAmountRequested());
            assertEquals(LoanReferralStatus.PENDING, referral.get().getLoanReferralStatus());
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
            loaneeBreakdowns.forEach(loaneeLoanBreakdown ->
                    loaneeLoanBreakDownRepository.deleteById(loaneeLoanBreakdown.getLoaneeLoanBreakdownId())
            );
            loaneeOutputPort.deleteLoanee(loaneeId);
            userIdentityOutputPort.deleteUserById(joel.getId());
            userIdentityOutputPort.deleteUserById(loaneeUserId);
            identityManagerOutputPort.deleteUser(loanee.getUserIdentity());
            loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);
            loanBreakdowns.forEach(tuitionBreakdown ->
                    loanBreakdownRepository.deleteById(tuitionBreakdown.getLoanBreakdownId())
            );
            cohortOutputPort.deleteCohort(cohortId);
            programOutputPort.deleteProgram(programId);
            Optional<LoanMetrics> loanMetrics = loanMetricsOutputPort.findByOrganizationId(amazingGrace.getId());
            if (loanMetrics.isPresent()) {
                loanMetricsOutputPort.delete(loanMetrics.get().getId());
            }
            List<OrganizationServiceOffering> organizationServiceOfferings = organizationIdentityOutputPort.
                    findOrganizationServiceOfferingsByOrganizationId(amazingGrace.getId());
            String serviceOfferingId = null;
            for (OrganizationServiceOffering organizationServiceOffering : organizationServiceOfferings) {
                serviceOfferingId = organizationServiceOffering.getServiceOffering().getId();
                organizationIdentityOutputPort.deleteOrganizationServiceOffering(organizationServiceOffering.getId());
            }
            organizationIdentityOutputPort.deleteServiceOffering(serviceOfferingId);
            organizationIdentityOutputPort.delete(amazingGrace.getId());
        } catch (MeedlException e) {
            log.error("Exception occurred cleaning up test data: ", e);
        }
    }
}
 