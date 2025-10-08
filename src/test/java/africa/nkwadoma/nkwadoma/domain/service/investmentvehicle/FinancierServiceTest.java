package africa.nkwadoma.nkwadoma.domain.service.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentvehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.*;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.OrganizationIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.notification.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.*;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.*;
import africa.nkwadoma.nkwadoma.domain.model.notification.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class FinancierServiceTest {
    @Autowired
    private FinancierUseCase financierUseCase;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private FinancierBeneficialOwnerOutputPort financierBeneficialOwnerOutputPort;
    @Autowired
    private BeneficialOwnerOutputPort beneficialOwnerOutputPort;
    @Autowired
    private InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort ;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Autowired
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
    private UserIdentity individualUserIdentity;
    private Financier individualFinancier;
    private String individualUserIdentityId;
    private String individualFinancierId;
    private UserIdentity cooperateUserIdentity;
    private Financier cooperateFinancier;
    private String cooperateUserIdentityId;
    private String cooperateFinancierId;
    private final String cooperateFinancierEmail = TestUtils.generateEmail("financierservicecooperatefinanciertest555", 5);
    private int pageSize = 10 ;
    private int pageNumber = 0 ;
    private final Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
    private BankDetail bankDetail;
    private String privateInvestmentVehicleId;
    private String publicInvestmentVehicleId;

    private List<Financier> individualFinancierList;
    private List<Financier> cooperateFinancierList;

    private InvestmentVehicle publicInvestmentVehicle;
    private InvestmentVehicle privateInvestmentVehicle;
    private UserIdentity portfolioManager;
    private String portfolioManagerId;
    private final String actorId = "ead0f7cb-5453-4bb8-b261-413790a9c364";
    private final BigDecimal FIVE_THOUSAND = new BigDecimal("5000.00");
    @Autowired
    private PoliticallyExposedPersonOutputPort politicallyExposedPersonOutputPort;
    @Autowired
    private FinancierPoliticallyExposedPersonOutputPort financierPoliticallyExposedPersonOutputPort;
    @Autowired
    private OrganizationIdentityOutputPort organizationIdentityOutputPort;
    private UserIdentity actor;


    @BeforeAll
    void setUp(){
        bankDetail = TestData.buildBankDetail();
        actor = TestData.createTestUserIdentity(String.format("userforcreatedbyoractor%s7@mail.com", TestUtils.generateName(3)), actorId);
        actor.setRole(IdentityRole.PORTFOLIO_MANAGER);
        try {
           actor = userIdentityOutputPort.save(actor);
        } catch (MeedlException e) {
            log.error("Error saving actor (pm) for invite financier.",e);
            throw new RuntimeException(e);
        }
        individualUserIdentity = TestData.createTestUserIdentity(String.format("financierserviceindividualfinancier%stest24@mail.com", TestUtils.generateName(3)),"ead0f7cb-5483-4bb8-b271-413990a9c368");
        individualUserIdentity.setRole(IdentityRole.FINANCIER);
        individualUserIdentity.setCreatedBy(actorId);
        deleteTestUserIfExist(individualUserIdentity);
        portfolioManager = TestData.createTestUserIdentity(String.format("portfoliomanager%stest6@gmail.com", TestUtils.generateName(3)));
        portfolioManager.setRole(IdentityRole.PORTFOLIO_MANAGER);

        cooperateUserIdentity = TestData.createTestUserIdentity(cooperateFinancierEmail, "ead0f7cb-5484-4bb8-b371-413950a9c367");
        cooperateUserIdentity.setCreatedBy(actorId);
        cooperateFinancier = buildCooperateFinancier(cooperateUserIdentity,  String.format("AlbertTestCooperationService%s", TestUtils.generateName(3)));
        cooperateFinancier.setName("AlbertTestCooperationService" + TestUtils.generateName(3));
        cooperateFinancier.setEmail(cooperateFinancierEmail);

        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle(String.format("FinancierVehicleForService%sTest", TestUtils.generateName(3)));
        publicInvestmentVehicle = TestData.buildInvestmentVehicle("publicInvestmentVehicleInTestClass");
        privateInvestmentVehicle = TestData.buildInvestmentVehicle("privateInvestmentVehicleInTestClass");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);

        privateInvestmentVehicleId = investmentVehicle.getId();

        individualFinancier = TestData.buildFinancierIndividual(individualUserIdentity);
        individualFinancier.setInvestmentVehicleId(privateInvestmentVehicleId);
        individualFinancierList = List.of(individualFinancier);

        cooperateFinancierList = List.of(cooperateFinancier);
    }

    private Financier buildCooperateFinancier(UserIdentity userIdentity , String companyName) {
        Cooperation cooperation = TestData.buildCooperation(companyName,"cooperate@grr.la");
        return TestData.buildCooperateFinancier(cooperation, userIdentity);
    }

    private void deleteTestUserIfExist(UserIdentity userIdentity) {
        try {
            Optional<UserIdentity> optionalFoundUserIdentity = identityManagerOutputPort.getUserByEmail(userIdentity.getEmail());
            if (optionalFoundUserIdentity.isPresent()){
                identityManagerOutputPort.deleteUser(optionalFoundUserIdentity.get());
            }
        } catch (MeedlException e) {
            log.error("Tried to find and delete test user before starting financier service test. Got this error: {}", e.getMessage(), e);
        }
    }

    private InvestmentVehicle createInvestmentVehicle(InvestmentVehicle investmentVehicle) {
        try {
            InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findByNameExcludingDraftStatus(investmentVehicle.getName(), InvestmentVehicleStatus.PUBLISHED);
            if (foundInvestmentVehicle == null){
                investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
            }else{
                investmentVehicle = foundInvestmentVehicle;
            }
        } catch (MeedlException e) {
            log.info("",e);
            throw new RuntimeException(e);
        }
        return investmentVehicle;
    }

    @Test
    @Order(1)
    public void inviteIndividualFinancierThatDoesNotExistOnThePlatformToInvestmentVehicle() {
        String response;
        Financier foundFinancier;
        try {
            assertThrows(MeedlException.class, ()-> userIdentityOutputPort.findByEmail(individualUserIdentity.getEmail()));
            response = financierUseCase.inviteFinancier(individualFinancierList, privateInvestmentVehicleId);
            log.info("the email in invite to platform test {}", individualUserIdentity.getEmail());
            individualUserIdentity = userIdentityOutputPort.findByEmail(individualUserIdentity.getEmail());
            individualUserIdentityId = individualUserIdentity.getId();
            foundFinancier = financierOutputPort.findFinancierByUserId(individualUserIdentityId);
            log.info("found financier {}", foundFinancier);
            individualFinancierId = foundFinancier.getId();
            log.info("Financier id for test user with id : {} is {}", individualUserIdentityId, individualFinancierId);
        } catch (MeedlException e) {
            log.info("Error occurred in financier service test --invite use case ", e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier has been added to an investment vehicle", response);
        assertEquals(ActivationStatus.PENDING_APPROVAL, foundFinancier.getActivationStatus());
        foundFinancier.setActivationStatus(ActivationStatus.ACTIVE);
        Page<Financier> financiers;
        try {
            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(privateInvestmentVehicleId, null, pageRequest);
            financierOutputPort.save(foundFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiers);
        assertFalse(financiers.isEmpty());
        assertEquals(individualFinancierId, financiers.getContent().get(0).getId());
        assertNotNull(individualFinancierId);
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidActorId(String actorId){
            UserIdentity actor = TestData.createTestUserIdentity("emailshouldnotsaveforanyreason@mail.com", actorId);
            Financier financier = TestData.buildFinancierIndividual(actor);
            Exception exception = assertThrows(MeedlException.class, ()->  financierUseCase.inviteFinancier(List.of(financier), privateInvestmentVehicleId));
            log.error("{}",exception.getMessage(), exception);
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        individualUserIdentity.setFirstName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(individualFinancier), privateInvestmentVehicleId));
    }
    @Test
    public void assignDesignationToFinancierWrongly()  {
        Set<InvestmentVehicleDesignation> investmentVehicleDesignations = new HashSet<>();
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.LEAD);
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.DONOR);
        individualFinancier.setInvestmentVehicleDesignation(investmentVehicleDesignations);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(individualFinancier), privateInvestmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(individualFinancier), privateInvestmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setUserIdentity(individualUserIdentity);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(individualFinancier), privateInvestmentVehicleId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        individualFinancier.getUserIdentity().setCreatedBy(invitedBy);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(individualFinancier), privateInvestmentVehicleId));
    }

    @Test
    @Order(2)
    void investInVehicle() {
        individualFinancier.setAmountToInvest(FIVE_THOUSAND);
        individualFinancier.setId(individualFinancierId);
        InvestmentVehicle investmentVehicle;
        Financier financier;
        try {
            investmentVehicle = investmentVehicleOutputPort.findById(privateInvestmentVehicleId);
            log.info("Financier id in invest in vehicle {}", individualFinancierId);
            financier = financierOutputPort.findById(individualFinancierId);
            assertNotNull(investmentVehicle.getTotalAvailableAmount());
            assertNotNull(financier);

            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals(new BigDecimal("0.00"), initialAmount);
            financierUseCase.investInVehicle(individualFinancier);

            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(privateInvestmentVehicleId);
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(individualFinancier.getAmountToInvest()), currentAmount,
                    "The total available amount should be updated correctly");
            List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(privateInvestmentVehicleId, individualFinancierId);
            assertFalse(investmentVehicleFinanciers.isEmpty());
            assertEquals(individualFinancier.getAmountToInvest(), investmentVehicleFinanciers.get(0).getAmountInvested(),
                    "The amount to invest should be updated correctly");
            financier = financierOutputPort.findById(individualFinancierId);
            log.info("found financier after investing");
            assertEquals(individualFinancier.getAmountToInvest(), financier.getTotalAmountInvested());
        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(3)
    void investInPrivateVehicleTwice() {
        BigDecimal amountToInvest = FIVE_THOUSAND;
        individualFinancier.setAmountToInvest(amountToInvest);
        individualFinancier.setId(individualFinancierId);
        InvestmentVehicle investmentVehicle = null;
        Financier financier = null;
        try {
            investmentVehicle = investmentVehicleOutputPort.findById(privateInvestmentVehicleId);
            financier = financierOutputPort.findById(individualFinancierId);
            log.info("Financier in invest in vehicle {}", financier);
            List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), individualFinancierId);
            assertNotNull(financier);
            assertFalse(investmentVehicleFinanciers.isEmpty());
            assertEquals(investmentVehicleFinanciers.get(0).getAmountInvested(), financier.getTotalAmountInvested());
            BigDecimal initialInvestedAmount = investmentVehicleFinanciers.get(0).getAmountInvested();
            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();

            assertEquals(FIVE_THOUSAND, initialAmount);

            financierUseCase.investInVehicle(individualFinancier);
            financier = financierOutputPort.findById(individualFinancierId);
             investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(privateInvestmentVehicleId, individualFinancierId);
            assertFalse(investmentVehicleFinanciers.isEmpty());
            assertTrue(investmentVehicleFinanciers.size() > BigInteger.ONE.intValue());
            BigDecimal totalInvestedAmount = initialInvestedAmount.add(individualFinancier.getAmountToInvest());
            assertEquals(totalInvestedAmount, financier.getTotalAmountInvested());
            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(privateInvestmentVehicleId);
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();

            assertEquals(initialAmount.add(FIVE_THOUSAND), currentAmount,
                    "The total available amount should be updated correctly");
            assertEquals(FIVE_THOUSAND, investmentVehicleFinanciers.get(0).getAmountInvested(),
                    "The amount to invest should be updated correctly");
            assertEquals(FIVE_THOUSAND, investmentVehicleFinanciers.get(1).getAmountInvested(),
                    "The amount to invest should be updated correctly");
        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(4)
    void investInPublicVehicle(){
        BigDecimal amountToInvest = new BigDecimal("5000.00");
        individualFinancier.setAmountToInvest(amountToInvest);
        individualFinancier.setId(individualFinancierId);
        InvestmentVehicle investmentVehicle;
        try {
            publicInvestmentVehicle.setInvestmentVehicleVisibility(InvestmentVehicleVisibility.PUBLIC);
            investmentVehicle = createInvestmentVehicle(publicInvestmentVehicle);
            publicInvestmentVehicleId = investmentVehicle.getId();
            individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());
            individualFinancier.setActivationStatus(ActivationStatus.ACTIVE);
            financierOutputPort.save(individualFinancier);
            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals(new BigDecimal("0.00"), initialAmount);
            if (investmentVehicle.getTotalAvailableAmount() == null) {
                investmentVehicle.setTotalAvailableAmount(BigDecimal.ZERO);
            }
            financierUseCase.investInVehicle(individualFinancier);

            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicle.getId());
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(individualFinancier.getAmountToInvest()), currentAmount,
                    "The total available amount should be updated correctly");
            List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), individualFinancierId);
            assertFalse(investmentVehicleFinanciers.isEmpty());
            assertTrue(investmentVehicleFinanciers.size() == BigInteger.ONE.intValue());
            assertEquals(individualFinancier.getAmountToInvest(), investmentVehicleFinanciers.get(0).getAmountInvested(),
                    "The amount to invest should be updated correctly");
            assertEquals(amountToInvest, investmentVehicleFinanciers.get(0).getAmountInvested());

        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(5)
    void investInPublicVehicleTwice() {
        individualFinancier.setAmountToInvest(FIVE_THOUSAND);
        individualFinancier.setId(individualFinancierId);
        InvestmentVehicle investmentVehicle = null;
        Financier financier = null;
        try {
            investmentVehicle = investmentVehicleOutputPort.findById(publicInvestmentVehicleId);
            financier = financierOutputPort.findById(individualFinancierId);
            List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), individualFinancierId);
            assertNotNull(investmentVehicleFinanciers);
            assertTrue(investmentVehicleFinanciers.size() == BigInteger.ONE.intValue());

            assertNotNull(financier);
            log.info("{}", investmentVehicleFinanciers.size());
            assertEquals(FIVE_THOUSAND, investmentVehicleFinanciers.get(0).getAmountInvested());
            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals( FIVE_THOUSAND, initialAmount);

            financierUseCase.investInVehicle(individualFinancier);

            financier = financierOutputPort.findById(individualFinancierId);
            investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), individualFinancierId);
            assertNotNull(investmentVehicleFinanciers);
            assertFalse(investmentVehicleFinanciers.isEmpty());
            assertNotNull(financier);
            log.info("{}", investmentVehicleFinanciers.size());
            assertTrue(investmentVehicleFinanciers.size() == BigInteger.TWO.intValue());

            BigDecimal totalAmountInvested = investmentVehicleFinanciers.stream()
                                                    .map(InvestmentVehicleFinancier::getAmountInvested)
                                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            assertEquals(totalAmountInvested, FIVE_THOUSAND.add(FIVE_THOUSAND));

            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(publicInvestmentVehicleId);
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(FIVE_THOUSAND), currentAmount, "The total available amount should be updated correctly");
            assertEquals(FIVE_THOUSAND, investmentVehicleFinanciers.get(0).getAmountInvested(),
                    "The amount invested should be updated correctly");
            assertEquals(FIVE_THOUSAND, investmentVehicleFinanciers.get(1).getAmountInvested());
            log.info("Total amount invested by financier in the platform. {}", financier.getTotalAmountInvested());
        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    @Test
    @Order(6)
    void viewAllFinancierInvestmentsByFinancier() {
        Page<Financier> financierInvestmemts = Page.empty();
        try {
            financierInvestmemts = financierUseCase.viewAllFinancierInvestment(individualUserIdentityId, "", pageSize, pageNumber);
        }catch (MeedlException e){
            log.info("{}",e.getMessage(), e);
        }
        assertFalse(financierInvestmemts.isEmpty());
        assertEquals(financierInvestmemts.getTotalElements(), BigInteger.valueOf(4).intValue());
    }

    @Test
    @Order(7)
    void searchFinancierInvestmentsByInvestmentVehicleName() {
        Page<Financier> financierInvestmemts = Page.empty();
        Financier financier = Financier.builder().investmentVehicleName("fi").actorId(individualUserIdentityId).
                pageNumber(0).pageSize(10).build();
        try {
            financierInvestmemts = financierUseCase.searchFinancierInvestment(financier);
        }catch (MeedlException e){
            log.info("{}",e.getMessage(), e);
        }
        assertFalse(financierInvestmemts.isEmpty());
        assertEquals(financierInvestmemts.getTotalElements(), BigInteger.valueOf(2).intValue());

    }

    @Test
    @Order(8)
    void searchFinancierInvestmentsByAnotherInvestmentVehicleName() {
        Page<Financier> financierInvestmemts = Page.empty();
        Financier financier = Financier.builder().investmentVehicleName("publ").actorId(individualUserIdentityId).
                pageNumber(0).pageSize(10).build();
        try {
            financierInvestmemts = financierUseCase.searchFinancierInvestment(
                    financier);
        }catch (MeedlException e){
            log.info("{}",e.getMessage(), e);
        }
        assertFalse(financierInvestmemts.isEmpty());
        assertEquals(financierInvestmemts.getTotalElements(), BigInteger.valueOf(2).intValue());
    }

    @Test
    @Order(9)
    void searchFinancierInvestmentsByTheSameVehicleName() {
        Page<Financier> financierInvestmemts = Page.empty();
        Financier financier = Financier.builder().investmentVehicleName("veh").actorId(individualUserIdentityId).
                pageNumber(0).pageSize(10).build();
        try {
            financierInvestmemts = financierUseCase.searchFinancierInvestment(
                    financier);
        }catch (MeedlException e){
            log.info("{}",e.getMessage(), e);
        }
        assertFalse(financierInvestmemts.isEmpty());
        assertEquals(financierInvestmemts.getTotalElements(), BigInteger.valueOf(4).intValue());
    }


    @Test
    void investInVehicleWithAmountLowerThanMinimumAmount() {
        InvestmentVehicle investmentVehicle = null;
        BigDecimal initialAmountInvested;
        InvestmentVehicle investmentVehicleToCheck;
        Financier foundFinancier;
        try {
            investmentVehicle = investmentVehicleOutputPort.findById(privateInvestmentVehicleId);
            initialAmountInvested = investmentVehicle.getAmountInvested();
            BigDecimal minimumInvestmentAmount = investmentVehicle.getMinimumInvestmentAmount();
            BigDecimal amountToInvest = new BigDecimal("1000");

            individualUserIdentity = userIdentityOutputPort.findByEmail(individualUserIdentity.getEmail());
            individualUserIdentityId = individualUserIdentity.getId();
            foundFinancier = financierOutputPort.findFinancierByUserId(individualUserIdentityId);
            foundFinancier.setAmountToInvest(amountToInvest);
            foundFinancier.setInvestmentVehicleId(privateInvestmentVehicleId);
            investmentVehicleToCheck = investmentVehicleOutputPort.findById(privateInvestmentVehicleId);
            log.info("Investment vehicle initial amount --- amount {} investment vehicle id {}", initialAmountInvested, privateInvestmentVehicleId);
        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }

        MeedlException exception = assertThrows(MeedlException.class, () -> financierUseCase.investInVehicle(foundFinancier));
        log.error("", exception);

        BigDecimal finalAmountInvested = investmentVehicleToCheck.getAmountInvested();
        log.info("finalAmountInvested {}, initialAmountInvested {}", finalAmountInvested, initialAmountInvested);
        assertEquals(finalAmountInvested, initialAmountInvested);
    }
    @Test
    void investInVehicleWithoutUserId(){
        individualFinancier.getUserIdentity().setId(null);
        MeedlException exception =assertThrows(MeedlException.class, ()->financierUseCase.investInVehicle(individualFinancier));
        log.info("",exception);
    }
    @Test
    void investInVehicleNullUserIdentity(){
        individualFinancier.setUserIdentity(null);
        assertThrows(MeedlException.class, ()->financierUseCase.investInVehicle(individualFinancier));
    }
    @Test
    void investInVehicleWithNullAmount(){
        individualFinancier.setAmountToInvest(null);
        MeedlException exception = assertThrows(MeedlException.class, ()-> financierUseCase.investInVehicle(individualFinancier));
        log.info("",exception);
    }

    @Test
    void investInVehicleWillNullInvestmentVehicleId(){
        individualFinancier= TestData.buildFinancierIndividual(individualUserIdentity);
        individualFinancier.setInvestmentVehicleId(null);
        assertThrows(MeedlException.class, ()-> financierUseCase.investInVehicle(individualFinancier));
    }
    @Test
    void investInVehicleWithNull(){
        assertThrows(MeedlException.class, ()-> financierUseCase.investInVehicle(null));
    }


    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(), privateInvestmentVehicleId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        individualUserIdentity.setFirstName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, privateInvestmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, privateInvestmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(individualFinancier), privateInvestmentVehicleId));
    }
    @Test
    void completeIndividualKycWithoutFinancierObject(){
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(null));
    }
    @Test
    void completeIndividualKycWithoutBankDetail(){
        individualFinancier.setUserIdentity(individualUserIdentity);
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.setUserIdentity(individualUserIdentity);
        financierWithKycRequest.getUserIdentity().setBankDetail(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountNumber(){
        individualFinancier.setUserIdentity(individualUserIdentity);
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankNumber(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountName(){
        individualFinancier.setUserIdentity(individualUserIdentity);
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankName(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberLessThanTen(){
        individualFinancier.setUserIdentity(individualUserIdentity);
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankNumber("123456789");
        assertThrows(MeedlException.class, ()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberGreaterThanFifteen(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankNumber("1234567890111213");
        assertThrows(MeedlException.class, ()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithoutTaxId(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().setTaxId(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutNin(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().setNin(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeKycWithNullUser(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.setUserIdentity(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    @Order(10)
    void completeKycWithBeneficialOwnerPercentageLessThanHundredPercent(){
        Financier financierUpdated = null;
        Financier foundFinancier = null;
        try {
            foundFinancier = financierUseCase.viewFinancierDetail(individualUserIdentityId, individualFinancierId);
            foundFinancier.setUserIdentity(individualUserIdentity);
            Financier financierWithKycRequest = TestData.completeKycRequest(foundFinancier, TestData.buildBankDetail());
            financierWithKycRequest.getBeneficialOwners().get(0).setPercentageOwnershipOrShare(1);
            assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
        } catch (MeedlException e) {
            log.info("===================> {}", e.getMessage(), e);
        }
    }

    @Test
    @Order(11)
    void completeKycWithBeneficialOwnerPercentageGreaterThanHundredPercent(){
        Financier financierUpdated = null;
        Financier foundFinancier = null;
        try {
            foundFinancier = financierUseCase.viewFinancierDetail(individualUserIdentityId, individualFinancierId);
            foundFinancier.setUserIdentity(individualUserIdentity);
            Financier financierWithKycRequest = TestData.completeKycRequest(foundFinancier, TestData.buildBankDetail());
            financierWithKycRequest.getBeneficialOwners().get(0).setPercentageOwnershipOrShare(101);
            assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
        } catch (MeedlException e) {
            log.info("===================> {}", e.getMessage(), e);
        }
    }

    @Test
    @Order(12)
    void completeKycIndividual() {
        Financier financierUpdated = null;
        Financier foundFinancier = null;
        Financier financierWithKycRequest = null;
        try {

            foundFinancier = financierUseCase.viewFinancierDetail(individualUserIdentityId, individualFinancierId);
            foundFinancier.setUserIdentity(individualUserIdentity);
            assertEquals(AccreditationStatus.UNVERIFIED, foundFinancier.getAccreditationStatus());
            log.info("financier found {} accreditation status  -------------> {}", foundFinancier, foundFinancier.getAccreditationStatus());
            assertNull(foundFinancier.getUserIdentity().getNextOfKin());
            financierWithKycRequest = TestData.completeKycRequest(foundFinancier, TestData.buildBankDetail());
            log.info("Financier before completing kyc individual : {}", financierWithKycRequest);
            financierWithKycRequest.setUserIdentity(individualUserIdentity);
            financierUpdated = financierUseCase.completeKyc(financierWithKycRequest);
            log.info("financier updated accreditation status completed kyc -------------> {}", financierUpdated);

        } catch (MeedlException e) {
            log.info("===================> {}", e.getMessage(), e);
        }
        assertNotNull(financierUpdated);
        assertNotNull(financierWithKycRequest);
        assertEquals(AccreditationStatus.VERIFIED, financierUpdated.getAccreditationStatus());
        //        assertNotNull(financierUpdated.getUserIdentity().getBankDetail());
        assertNotNull(financierUpdated.getSourceOfFunds());
        assertEquals(financierUpdated.getSourceOfFunds(), financierWithKycRequest.getSourceOfFunds());
//        assertEquals(financierUpdated.getOccupation(), financierWithKycRequest.getOccupation());
        assertNotNull(financierUpdated.getBeneficialOwners());
        assertFalse(financierUpdated.getBeneficialOwners().isEmpty());
        assertNotNull(financierUpdated.getBeneficialOwners().get(0));
        assertNotNull(financierUpdated.getBeneficialOwners().get(0).getCountryOfIncorporation());

    }

    @Test
    @Order(13)
    void viewAllFinanciers(){
        Page<Financier> financiersPage = null;
        try {
            individualFinancier.setActivationStatus(null);
            financiersPage = financierUseCase.viewAllFinancier(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        List<Financier> financiers = financiersPage.toList();
        assertFalse(financiers.isEmpty());
    }

    @Test
    void viewAllActiveFinancier(){
        Page<Financier> financiersPage = null;
        try{
            individualFinancier.setActivationStatuses(List.of(ActivationStatus.ACTIVE));
            financiersPage = financierUseCase.viewAllFinancier(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        assertThat(financiersPage).allMatch(financier -> financier.getActivationStatus().equals(ActivationStatus.ACTIVE));
    }

    @Test
    void viewAllInvitedFinancier(){
        Page<Financier> financiersPage = null;
        try{
            individualFinancier.setActivationStatus(ActivationStatus.INVITED);
            Financier foundFinancier = financierOutputPort.findById(individualFinancierId);
            assertNotNull(foundFinancier);
            foundFinancier.setActivationStatuses(List.of(ActivationStatus.INVITED));
            financierOutputPort.save(foundFinancier);
            financiersPage = financierOutputPort.viewAllFinancier(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        assertThat(financiersPage).allMatch(financier -> financier.getActivationStatus().equals(ActivationStatus.INVITED));
    }

    @Test
    void viewAllInactiveFinancier(){
        Page<Financier> financiersPage = null;
        try{
            individualFinancier.setActivationStatuses(List.of(ActivationStatus.INACTIVE));
            Financier foundFinancier = financierOutputPort.findById(individualFinancierId);
            assertNotNull(foundFinancier);
            foundFinancier.setActivationStatus(ActivationStatus.INACTIVE);
            financierOutputPort.save(foundFinancier);
            financiersPage = financierOutputPort.viewAllFinancier(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        assertThat(financiersPage).allMatch(financier -> financier.getActivationStatus().equals(ActivationStatus.INACTIVE));
    }

    @Test
    void viewAllDeactivatedFinancier(){
        Page<Financier> financiersPage = null;
        try{
            individualFinancier.setActivationStatuses(List.of(ActivationStatus.DEACTIVATED));
            Financier foundFinancier = financierOutputPort.findById(individualFinancierId);
            assertNotNull(foundFinancier);
            foundFinancier.setActivationStatus(ActivationStatus.DEACTIVATED);
            financierOutputPort.save(foundFinancier);
            financiersPage = financierOutputPort.viewAllFinancier(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        assertThat(financiersPage).allMatch(financier -> financier.getActivationStatus().equals(ActivationStatus.DEACTIVATED));
    }

    @Test
    @Order(14)
    void findFinancierById() {
        Financier foundFinancier = null;
        try {
            foundFinancier = financierUseCase.viewFinancierDetail(individualUserIdentityId, individualFinancierId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals(individualFinancierId, foundFinancier.getId());
        assertNotNull(foundFinancier.getSourceOfFunds());
        assertNotNull(foundFinancier.getBeneficialOwners());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ndnifeif"})
    void findFinancierByInvalidId(String invalidId) {
        assertThrows(MeedlException.class, ()-> financierUseCase.viewFinancierDetail(invalidId, individualFinancierId));
    }
    @Test
    @Order(15)
    public void viewAllFinancierInInvestmentVehicle() {
        Page<Financier> financiersPage = null;
        individualFinancier.setInvestmentVehicleId(privateInvestmentVehicleId);
        individualFinancier.setActivationStatus(null);
        try {
            financiersPage = financierUseCase.viewAllFinancierInInvestmentVehicle(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        List<Financier> financiers = financiersPage.toList();
        assertFalse(financiers.isEmpty());
        assertEquals(individualFinancierId, financiers.get(0).getId());
        assertNotNull(financiers.get(0).getInvestmentVehicleDesignation());
        assertFalse(financiers.get(0).getInvestmentVehicleDesignation().isEmpty());
    }
    @Test
    void viewAllFinancierInVehicleWithNull(){
        assertThrows(MeedlException.class,()-> financierUseCase.viewAllFinancierInInvestmentVehicle(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "hidhfj"})
    void viewAllFinanciersInInvestmentVehicleWithInvalidVehicleId(String invalidId) {
        individualFinancier.setInvestmentVehicleId(invalidId);
        assertThrows(MeedlException.class,()-> financierUseCase.viewAllFinancierInInvestmentVehicle(individualFinancier));
    }
    @Test
    void viewFinanciersWithNull(){
        assertThrows(MeedlException.class,()-> financierUseCase.viewAllFinancier(null));
    }

    @Test
    public void inviteFinancierWithInvalidOrNonExistingInvestmentVehicleId(){
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, "invalid investment vehicle id"));
    }
    @Test
    @Order(16)
    void viewAllFinancierInVehicleWithActivationStatus(){
        Page<Financier> financiersPage = null;
        try {
            financiersPage = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(privateInvestmentVehicleId,null, pageRequest);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        List<Financier> financiers = financiersPage.toList();
        assertNotNull(financiers.get(0).getActivationStatus());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ervkdldd"})
    void viewAllFinancierInVehicleWithStatusAndInvalidVehicleId(String invalidId) {
        assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(invalidId, List.of(ActivationStatus.INVITED), pageRequest));
    }
//    @Test
//    @Order(17)
//    public void inviteCooperateFinancierToNewVehicle() {
//
//        UserIdentity cooperateUserIdentity = TestData.createTestUserIdentity(TestUtils.generateEmail("cooperateFinancierEmailtest", 5), "ead0f7cb-5484-4bb8-b371-433850a9c367");
//        cooperateUserIdentity.setCreatedBy(actorId);
//        Financier cooperateFinancier = buildCooperateFinancier(cooperateUserIdentity,  TestUtils.generateName("NewVehicleCooperationTestCooperationService" ,4));
//
//        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle(TestUtils.generateName("FinancierVehicleForCooperateServiceTest",4));
//        investmentVehicle = createInvestmentVehicle(investmentVehicle);
//        List<Financier> cooperateFinancierList = List.of(cooperateFinancier);
//
//        String response;
//        try {
//            response = financierUseCase.inviteFinancier(cooperateFinancierList, investmentVehicle.getId());
//        } catch (MeedlException e) {
//            log.error("Failed to invite with error {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//        assertNotNull(response);
//        assertEquals("Financier has been added to an investment vehicle", response);
//        Page<Financier> financiers;
//        try {
//            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicle.getId(), null, pageRequest);
//            cooperateFinancier = financierOutputPort.findFinancierByUserId(cooperateUserIdentity.getId());
//            deleteInvestmentVehicleFinancier(investmentVehicle.getId(), cooperateFinancier.getId());
//            financierOutputPort.delete(cooperateFinancier.getId());
//            deleteNotification(cooperateUserIdentity.getId());
//            userIdentityOutputPort.deleteUserById(cooperateUserIdentity.getId());
//            identityManagerOutputPort.deleteUser(cooperateUserIdentity);
//            investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
//        } catch (MeedlException e) {
//            throw new RuntimeException(e);
//        }
//        assertNotNull(financiers);
//        assertFalse(financiers.isEmpty());
//        assertNotNull(cooperateFinancier.getId());
//        assertEquals(cooperateFinancier.getId(), financiers.getContent().get(0).getId());
//        assertEquals(ActivationStatus.INVITED, financiers.getContent().get(0).getActivationStatus());
//        assertEquals(AccreditationStatus.UNVERIFIED, financiers.getContent().get(0).getAccreditationStatus());
//
//    }
    @Test
    public void inviteFinancierToNoneExistentInvestmentVehicle(){
        individualFinancier.setInvestmentVehicleId("61fb3beb-f200-4b16-ac58-c28d737b546c");
        assertThrows(MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, privateInvestmentVehicleId));
    }
    @Test
    public void inviteLoaneeToBecomeAFinancier() {
//        UserIdentity loanee = TestData.createTestUserIdentity("loaneeemailservicetest@mail.com");
//        try {
//            loanee = userIdentityOutputPort.save(loanee);
//        } catch (MeedlException e) {
//            throw new RuntimeException(e);
//        }
//        assertEquals(IdentityRole.LOANEE, loanee.getRole());
//        financier.setIndividual(loanee);
//
//        String response;
//        Financier foundFinancier;
//        try {
//            response = financierUseCase.inviteFinancier(financier);
//            foundFinancier = financierOutputPort.findFinancierByUserId(loanee.getId());
//        } catch (MeedlException e) {
//            throw new RuntimeException(e);
//        }
//        assertNotNull(response);
//        assertEquals("Financier added to investment vehicle", response);
//        assertNotNull(foundFinancier);
//        assertNotNull(foundFinancier.getIndividual());
//        assertEquals(loanee.getId(), foundFinancier.getIndividual().getId());
//        assertEquals(IdentityRole.LOANEE, foundFinancier.getIndividual().getRole());

    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findByInvalidName(String name){
        individualFinancier.setInvestmentVehicleId(null);
        assertThrows(MeedlException.class,()-> financierUseCase.search(name, individualFinancier));
    }
    @Test
    @Order(18)
    void searchFinancierByFirstName()  {
        Page<Financier> foundFinanciers = null;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getFirstName(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(19)
    void searchFinancierByLastName() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getLastName(),individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(20)
    void searchFinancierWithFirstNameBeforeLastName() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getFirstName() +" "+ individualFinancier.getUserIdentity().getLastName(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(21)
    void searchFinancierWithLastNameBeforeFirstName() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getLastName() +" "+ individualFinancier.getUserIdentity().getFirstName(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(22)
    void searchFinancierWithEmail() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getEmail(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(23)
    void searchFinancierInVehicle() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getLastName(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        log.info("{}", foundFinanciers.stream().count());
        assertNotNull(foundFinanciers.getContent().get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ijk"})
    void viewInvestmentDetailOfFinancierWithNullFinancierId(String financierId){
        assertThrows(MeedlException.class, ()-> financierUseCase.viewInvestmentDetailOfFinancier(financierId, portfolioManagerId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ijk"})
    void viewInvestmentDetailOfFinancierWithNullPortfolioManagerId(String pmId){
        assertThrows(MeedlException.class, ()-> financierUseCase.viewInvestmentDetailOfFinancier(individualFinancierId, pmId));
    }

    @Test
    void viewInvestmentDetailOfFinancierWithNonExistingUserId(){
        String nonExistingUserId = "52eff78e-f01e-413e-93e6-8073f06ba25c";
        assertThrows(MeedlException.class, ()->financierUseCase.viewInvestmentDetailOfFinancier(individualFinancierId, nonExistingUserId));
    }

    @Test
    void viewInvestmentDetailOfFinancierWithNonExistingFinancierId(){
        String nonExistingFinancierId = "52eff78e-f01e-413e-93e6-8073f06ba25c";
        Exception ex = assertThrows(MeedlException.class, ()->financierUseCase.viewInvestmentDetailOfFinancier(individualFinancierId, nonExistingFinancierId));
    }

    @Test
    @Order(24)
    void viewInvestmentDetailOfFinancierByPortfolioManager(){
        FinancierVehicleDetail foundFinancierDetail = null;
        try {
            UserIdentity manager = userIdentityOutputPort.save(portfolioManager);
            portfolioManagerId = manager.getId();
            foundFinancierDetail = financierUseCase.viewInvestmentDetailOfFinancier(individualFinancierId, portfolioManagerId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancierDetail);
        assertNotNull(foundFinancierDetail.getInvestmentSummaries());
        assertFalse(foundFinancierDetail.getInvestmentSummaries().isEmpty());
        assertNotNull(foundFinancierDetail.getInvestmentSummaries().get(0).getName());
    }

    @Test
    @Order(25)
    public void inviteCooperateFinancierToPlatform() {
        Financier foundFinancier;
        String inviteResponse;
        try {
            cooperateFinancier.setInvestmentVehicleId(null);
            inviteResponse = financierUseCase.inviteFinancier(cooperateFinancierList, null);
            log.info("Saved cooperate financier: {}", inviteResponse);
            cooperateUserIdentity = userIdentityOutputPort.findByEmail(cooperateFinancierEmail);
            cooperateUserIdentityId = cooperateUserIdentity.getId();
            foundFinancier = financierOutputPort.findFinancierByCooperateStaffUserId(cooperateUserIdentityId);
            cooperateFinancierId = foundFinancier.getId();
        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals(FinancierType.COOPERATE, foundFinancier.getFinancierType());
        assertEquals(ActivationStatus.PENDING_APPROVAL, foundFinancier.getActivationStatus());

        List<InvestmentVehicleFinancier> investmentVehicleFinanciers = null;
        try {
            investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(privateInvestmentVehicleId, cooperateFinancierId);
        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }

        assertTrue(investmentVehicleFinanciers.isEmpty());
    }
//    @Test
//    @Order(27)
//    public void inviteCooperateFinancierToNewVehicleWithAmountToInvest() {
//
//        UserIdentity cooperateUserIdentity = TestData.createTestUserIdentity(String.format("cooperateFinancierEmailtestwith%samount@email.com", TestUtils.generateName(3)), "ead0f7cb-5484-4bb8-b371-433850a9c367");
//        cooperateUserIdentity.setCreatedBy(actorId);
//        Financier cooperateFinancier = buildCooperateFinancier(cooperateUserIdentity,  String.format("NewVehicleCooperationTestCooperationServiceWithAmountToInvest%s", TestUtils.generateName(3)));
//
//        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForCooperateServiceTestWithFinancierAmountToInvest");
//        investmentVehicle = createInvestmentVehicle(investmentVehicle);
//        cooperateFinancier.setAmountToInvest(new BigDecimal("10000.00"));
//        List<Financier> cooperateFinancierList = List.of(cooperateFinancier);
//
//        String response;
//        Financier foundFinancier;
//        try {
//            assertNotNull(investmentVehicle.getTotalAvailableAmount());
//            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
//            assertEquals(new BigDecimal("000.00"), initialAmount);
//
//            response = financierUseCase.inviteFinancier(cooperateFinancierList, investmentVehicle.getId());
//
//            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicle.getId());
//            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
//            assertEquals(initialAmount.add(cooperateFinancier.getAmountToInvest()), currentAmount,
//                    "The total available amount should be updated correctly");
//
//            foundFinancier = financierOutputPort.findFinancierByEmail(cooperateFinancier.getUserIdentity().getEmail());
//
//            List<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), foundFinancier.getId());
//            assertFalse(investmentVehicleFinancier.isEmpty());
//            assertEquals(cooperateFinancier.getAmountToInvest(), investmentVehicleFinancier.get(0).getAmountInvested(),
//                    "The amount to invest should be updated correctly");
//        } catch (MeedlException e) {
//            log.error("Failed to invite with error {}", e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//        assertNotNull(response);
//        assertEquals("Financier has been added to an investment vehicle", response);
//        Page<Financier> financiers;
//        try {
//            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicle.getId(), null ,pageRequest);
//            cooperateFinancier = foundFinancier;
//            investmentVehicleFinancierOutputPort.deleteByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), cooperateFinancier.getId());
//            deleteInvestmentVehicleFinancier(investmentVehicle.getId(), cooperateFinancier.getId());
//            financierOutputPort.delete(cooperateFinancier.getId());
//            deleteNotification(cooperateUserIdentity.getId());
//            userIdentityOutputPort.deleteUserById(cooperateUserIdentity.getId());
//            identityManagerOutputPort.deleteUser(cooperateUserIdentity);
//            investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
//        } catch (MeedlException e) {
//            throw new RuntimeException(e);
//        }
//        assertNotNull(financiers);
//        assertFalse(financiers.isEmpty());
//        assertNotNull(cooperateFinancier.getId());
//        assertEquals(cooperateFinancier.getId(), financiers.getContent().get(0).getId());
//        assertEquals(ActivationStatus.INVITED, financiers.getContent().get(0).getActivationStatus());
//        assertEquals(AccreditationStatus.UNVERIFIED, financiers.getContent().get(0).getAccreditationStatus());
//
//    }

    @Test
    void viewInvestmentDetailWithNonExistingFinancierId(){
        String testFinancierId = "547391e5-19be-42d6-b725-f7df35138dfb";
        Exception exception = assertThrows(MeedlException.class, ()->financierOutputPort.findById(testFinancierId));
        log.info("------->Exception message------------>"+exception.getMessage());
    }

    @Test
    @Order(28)
    public void acceptPrivacyPolicy() throws MeedlException {
        individualFinancier.setPrivacyPolicyAccepted(Boolean.TRUE);
        String message = financierUseCase.makePrivacyPolicyDecision(individualFinancier);
        assertNotNull(message);
        Financier foundFinancier = financierOutputPort.findByFinancierId(individualFinancierId);
        assertNotNull(foundFinancier);
        assertTrue(foundFinancier.isPrivacyPolicyAccepted());

    }
    @Test
    @Order(29)
    public void declinePrivacyPolicy() throws MeedlException {
        individualFinancier.setPrivacyPolicyAccepted(Boolean.FALSE);
        individualUserIdentity.setId(individualUserIdentityId);
        individualFinancier.setUserIdentity(individualUserIdentity);
        String message = financierUseCase.makePrivacyPolicyDecision(individualFinancier);
        assertNotNull(message);
        Financier foundFinancier = financierOutputPort.findById(individualFinancierId);
        assertNotNull(foundFinancier);
        assertFalse(foundFinancier.isPrivacyPolicyAccepted());

    }
    @AfterAll
    void tearDown() throws MeedlException {

        log.info("Started deleting data in financier service test." );
        individualUserIdentity.setId(individualUserIdentityId);
        deleteNotification(individualUserIdentityId);
        deleteInvestmentVehicleFinancier(privateInvestmentVehicleId, individualFinancierId);
        deleteInvestmentVehicleFinancier(privateInvestmentVehicleId, individualFinancierId);
        deleteInvestmentVehicleFinancier(publicInvestmentVehicleId, individualFinancierId);
        deleteFinancierPoliticallyExposedPeople(cooperateFinancierId);
        deleteFinancierPoliticallyExposedPeople(individualFinancierId);

        deleteFinancierData(individualFinancierId);
        identityManagerOutputPort.deleteUser(individualUserIdentity);
        userIdentityOutputPort.deleteUserById(individualUserIdentityId);
        userIdentityOutputPort.deleteUserById(actor.getId());
        deleteNotification(portfolioManagerId);
        userIdentityOutputPort.deleteUserById(portfolioManagerId);

        deleteNotification(individualUserIdentityId);
        deleteInvestmentVehicleFinancier(privateInvestmentVehicleId, individualFinancierId);


        organizationIdentityOutputPort.delete(cooperateFinancier.getIdentity());

        deleteFinancierData(cooperateFinancierId);
        cooperateUserIdentity.setId(cooperateUserIdentityId);
        identityManagerOutputPort.deleteUser(cooperateUserIdentity);
        userIdentityOutputPort.deleteUserById(cooperateUserIdentityId);

        investmentVehicleOutputPort.deleteInvestmentVehicle(privateInvestmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(publicInvestmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(privateInvestmentVehicleId);

        log.info("Test data deleted after test");
    }

    private void deleteFinancierPoliticallyExposedPeople(String financierId) throws MeedlException {
        List<FinancierPoliticallyExposedPerson> financierPoliticallyExposedPeople = financierPoliticallyExposedPersonOutputPort.findAllByFinancierId(financierId);
        log.info("Financier politically exposed size : {}", financierPoliticallyExposedPeople.size());
        financierPoliticallyExposedPeople
                        .forEach(financierPoliticallyExposedPerson -> {
                            try {
                                financierPoliticallyExposedPersonOutputPort.deleteById(financierPoliticallyExposedPerson.getId());
                                politicallyExposedPersonOutputPort.deleteById(financierPoliticallyExposedPerson.getPoliticallyExposedPerson().getId());
                                log.info("politically exposed deleted successfully. single {}, joined {}",financierPoliticallyExposedPerson.getPoliticallyExposedPerson().getId(), financierPoliticallyExposedPerson.getId());
                            } catch (MeedlException e) {
                                log.error("Error deleting politically exposed person.", e);
                                throw new RuntimeException(e);
                            }
                        });
        log.info("End of deleting");


    }

    private void deleteFinancierData(String financierId) throws MeedlException {
        List<FinancierBeneficialOwner> financierBeneficialOwners = financierBeneficialOwnerOutputPort.findAllByFinancierId(financierId);
        financierBeneficialOwners
                        .forEach(financierBeneficialOwner -> {
                            try {
                                log.info("Beneficial owner {}", financierBeneficialOwner);
                                financierBeneficialOwnerOutputPort.deleteById(financierBeneficialOwner.getId());
                                beneficialOwnerOutputPort.deleteById(financierBeneficialOwner.getBeneficialOwner().getId());
                            } catch (MeedlException e) {
                                log.warn("Delete test data financier ", e);
                                throw new RuntimeException(e);
                            }
                        });
        financierOutputPort.delete(financierId);
    }

    private void deleteInvestmentVehicleFinancier(String investmentVehicleId, String financierId) throws MeedlException {
        List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicleId, financierId);
      investmentVehicleFinanciers.forEach(investmentVehicleFinancier -> {
          try {
              investmentVehicleFinancierOutputPort.deleteInvestmentVehicleFinancier(investmentVehicleFinancier.getId());
          } catch (MeedlException e) {
              log.info("Test delete investment vehicle financier");
              throw new RuntimeException(e);
          }
      });

    }

//    private void deleteNotification(String userIdentityId) throws MeedlException {
//        Page<MeedlNotification> meedlNotifications = meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userIdentityId,pageSize,pageNumber);
//        meedlNotifications.forEach(notification-> {
//            try {
//                meedlNotificationOutputPort.deleteNotification(notification.getId());
//                log.info("Deleting notifications for test user with id {} and notification with id {}", userIdentityId, notification.getId());
//            } catch (MeedlException e) {
//                log.warn("Unable to delete notification for test user with id {}", userIdentityId);
//                throw new RuntimeException(e);
//            }
//        });
//    }
    private void deleteNotification(String userIdentityId) throws MeedlException {
        Page<MeedlNotification> notifications = meedlNotificationOutputPort
                            .findAllNotificationBelongingToAUser(userIdentityId, pageSize, pageNumber);
        if (CollectionUtils.isNotEmpty(notifications.stream().toList())) {
            meedlNotificationOutputPort.deleteMultipleNotification(userIdentityId,
                    notifications.stream().map(MeedlNotification::getId).toList());
        }
    }
}

