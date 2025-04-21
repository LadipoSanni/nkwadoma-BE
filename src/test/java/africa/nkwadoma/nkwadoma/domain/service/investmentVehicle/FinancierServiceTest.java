package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleVisibility;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.FinancierVehicleDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
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
import java.util.*;

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
    private InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort ;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Autowired
    private CooperationOutputPort cooperationOutputPort;
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
    private final String cooperateFinancierEmail = "financierservicecooperatefinanciertest24@mail.com";
    private int pageSize = 10 ;
    private int pageNumber = 0 ;
    private final Pageable pageRequest = PageRequest.of(pageNumber, pageSize);
    private BankDetail bankDetail;
    private String investmentVehicleId;
    private String privateInvestmentVehicleId;
    private String publicInvestmentVehicleId;

    private List<Financier> individualFinancierList;
    private List<Financier> cooperateFinancierList;
    private NextOfKin nextOfKin;

    private InvestmentVehicle publicInvestmentVehicle;
    private InvestmentVehicle privateInvestmentVehicle;
    private UserIdentity portfolioManager;
    private String portfolioManagerId;

    @BeforeAll
    void setUp(){
        bankDetail = TestData.buildBankDetail();
        individualUserIdentity = TestData.createTestUserIdentity("financierserviceindividualfinanciertest2@mail.com","ead0f7cb-5483-4bb8-b271-413990a9c368");
        individualUserIdentity.setRole(IdentityRole.FINANCIER);
        deleteTestUserIfExist(individualUserIdentity);
        portfolioManager = TestData.createTestUserIdentity("manager@gmail.com");
        portfolioManager.setRole(IdentityRole.PORTFOLIO_MANAGER);

        cooperateUserIdentity = TestData.createTestUserIdentity(cooperateFinancierEmail, "ead0f7cb-5484-4bb8-b371-413950a9c367");
        cooperateFinancier = buildCooperateFinancier(cooperateUserIdentity,  "AlbertTestCooperationService" );

        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForServiceTest");
        publicInvestmentVehicle = TestData.buildInvestmentVehicle("publicInvestmentVehicleInTestClass");
        privateInvestmentVehicle = TestData.buildInvestmentVehicle("privateInvestmentVehicleInTestClass");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);

        investmentVehicleId = investmentVehicle.getId();

        individualFinancier = TestData.buildFinancierIndividual(individualUserIdentity);
        individualFinancier.setInvestmentVehicleId(investmentVehicleId);
        individualFinancierList = List.of(individualFinancier);
        nextOfKin = TestData.createNextOfKinData(individualFinancier.getUserIdentity());

        cooperateFinancierList = List.of(cooperateFinancier);
    }

    private Financier buildCooperateFinancier(UserIdentity userIdentity , String companyName) {
        Cooperation cooperation = TestData.buildCooperation(companyName);
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
                investmentVehicle.setTotalAvailableAmount(investmentVehicle.getSize());
//                investmentVehicle.setInvestmentVehicleDesignation(Set.of(InvestmentVehicleDesignation.SPONSOR, InvestmentVehicleDesignation.ENDOWER));
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
            response = financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId);
            individualUserIdentity = userIdentityOutputPort.findByEmail(individualUserIdentity.getEmail());
            individualUserIdentityId = individualUserIdentity.getId();
            foundFinancier = financierOutputPort.findFinancierByUserId(individualUserIdentityId);
            individualFinancierId = foundFinancier.getId();
            log.info("Financier id for test user with id : {} is {}", individualUserIdentityId, individualFinancierId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier has been added to investment vehicle", response);
        assertEquals(ActivationStatus.INVITED, foundFinancier.getActivationStatus());
        foundFinancier.setActivationStatus(ActivationStatus.ACTIVE);
        try {
            financierOutputPort.save(foundFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void inviteFinancierToPlatform(){

    }
    @Test
    public void inviteFinancierOnPlatformToAnotherInvestmentVehicle() {

    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        individualUserIdentity.setFirstName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }
    @Test
    public void assignDesignationToFinancierWrongly()  {
        Set<InvestmentVehicleDesignation> investmentVehicleDesignations = new HashSet<>();
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.LEAD);
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.DONOR);
        individualFinancier.setInvestmentVehicleDesignation(investmentVehicleDesignations);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setUserIdentity(individualUserIdentity);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        individualFinancier.getUserIdentity().setCreatedBy(invitedBy);
        individualFinancier.setId(null);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }

    @Test
    @Order(2)
    void investInVehicle() {
        individualFinancier.setAmountToInvest(new BigDecimal("1000.00"));
        individualFinancier.setId(individualFinancierId);
        InvestmentVehicle investmentVehicle;
        Financier financier;
        try {
            investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            financier = financierOutputPort.findFinancierByFinancierId(individualFinancierId);
            assertNotNull(investmentVehicle.getTotalAvailableAmount());
            assertNotNull(financier);

            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals(new BigDecimal("4000.00"), initialAmount);
            financierUseCase.investInVehicle(individualFinancier);

            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(individualFinancier.getAmountToInvest()), currentAmount,
                    "The total available amount should be updated correctly");
            Optional<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, individualFinancierId);
            assertTrue(investmentVehicleFinancier.isPresent());
            assertEquals(individualFinancier.getAmountToInvest(), investmentVehicleFinancier.get().getAmountInvested(),
                    "The amount to invest should be updated correctly");
            financier = financierOutputPort.findFinancierByFinancierId(individualFinancierId);
            assertEquals(individualFinancier.getAmountToInvest(), financier.getTotalAmountInvested());
        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(3)
    void investInPrivateVehicleTwice() {
        individualFinancier.setAmountToInvest(new BigDecimal("1000.00"));
        individualFinancier.setId(individualFinancierId);
        InvestmentVehicle investmentVehicle = null;
        Financier financier = null;
        try {
            investmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            financier = financierOutputPort.findFinancierByFinancierId(individualFinancierId);
            Optional<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, individualFinancierId);
            assertTrue(investmentVehicleFinancier.isPresent());
            assertEquals(investmentVehicleFinancier.get().getAmountInvested(), financier.getTotalAmountInvested());
            BigDecimal initialInvestedAmount = investmentVehicleFinancier.get().getAmountInvested();
            assertNotNull(financier);
            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals( new BigDecimal("5000.00"), initialAmount);
            if (investmentVehicle.getTotalAvailableAmount() == null) {
                investmentVehicle.setTotalAvailableAmount(BigDecimal.ZERO);
            }
            financierUseCase.investInVehicle(individualFinancier);
            financier = financierOutputPort.findFinancierByFinancierId(individualFinancierId);
            investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, individualFinancierId);
            assertTrue(investmentVehicleFinancier.isPresent());
            BigDecimal totalInvestedAmount = initialInvestedAmount.add(individualFinancier.getAmountToInvest());
            assertEquals(totalInvestedAmount, financier.getTotalAmountInvested());
            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(individualFinancier.getAmountToInvest()), currentAmount,
                    "The total available amount should be updated correctly");
            assertEquals(individualFinancier.getAmountToInvest().add(new BigDecimal("1000.00")), investmentVehicleFinancier.get().getAmountInvested(),
                    "The amount to invest should be updated correctly");
        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(4)
    void investInPublicVehicle(){
        individualFinancier.setAmountToInvest(new BigDecimal("5000.00"));
        individualFinancier.setId(individualFinancierId
        );
        InvestmentVehicle investmentVehicle;
        try {
            publicInvestmentVehicle.setTotalAvailableAmount(publicInvestmentVehicle.getSize());
            publicInvestmentVehicle.setInvestmentVehicleVisibility(InvestmentVehicleVisibility.PUBLIC);
            investmentVehicle = createInvestmentVehicle(publicInvestmentVehicle);
            publicInvestmentVehicleId = investmentVehicle.getId();
            individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());
            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals(new BigDecimal("4000.00"), initialAmount);
            if (investmentVehicle.getTotalAvailableAmount() == null) {
                investmentVehicle.setTotalAvailableAmount(BigDecimal.ZERO);
            }
            financierUseCase.investInVehicle(individualFinancier);

            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicle.getId());
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(individualFinancier.getAmountToInvest()), currentAmount,
                    "The total available amount should be updated correctly");
            Optional<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), individualFinancierId);
            assertTrue(investmentVehicleFinancier.isPresent());
            assertEquals(individualFinancier.getAmountToInvest(), investmentVehicleFinancier.get().getAmountInvested(),
                    "The amount to invest should be updated correctly");
        } catch (MeedlException e) {
            log.info("{}",e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Test
    void investInVehicleWithNullAmount(){
        individualFinancier.setAmountToInvest(null);
        assertThrows(MeedlException.class, ()-> financierUseCase.investInVehicle(individualFinancier));
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
        assertThrows(MeedlException.class,()-> financierUseCase.inviteFinancier(List.of(), investmentVehicleId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        individualUserIdentity.setFirstName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList,investmentVehicleId));
    }
    @Test
    void completeIndividualKycWithoutFinancierObject(){
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(null));
    }
    @Test
    void completeIndividualKycWithoutBankDetail(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().setBankDetail(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountNumber(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankNumber(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountName(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankName(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberLessThanTen(){
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
    @Order(5)
    void completeKycIndividual() {
        Financier financierUpdated = null;
        Financier foundFinancier = null;
        Financier financierWithKycRequest = null;
        try {
            foundFinancier = financierUseCase.viewFinancierDetail(individualUserIdentityId, individualFinancierId);
            assertNotNull(foundFinancier.getUserIdentity());
            assertEquals(AccreditationStatus.UNVERIFIED, foundFinancier.getAccreditationStatus());
            log.info("financier found {} accreditation status  -------------> {}", foundFinancier, foundFinancier.getAccreditationStatus());
            assertNull(foundFinancier.getUserIdentity().getNextOfKin());
            nextOfKin.setUserId(foundFinancier.getUserIdentity().getId());
            financierWithKycRequest = TestData.completeKycRequest(foundFinancier, TestData.buildBankDetail());
            log.info("Financier before completing kyc individual : {}", financierWithKycRequest);
            financierUpdated = financierUseCase.completeKyc(financierWithKycRequest);
            log.info("financier updated accreditation status completed kyc -------------> {}", financierUpdated);

        } catch (MeedlException e) {
            log.info("===================> {}", e.getMessage(), e);
        }
        assertNotNull(financierUpdated);
        assertNotNull(financierWithKycRequest);
        assertEquals(AccreditationStatus.VERIFIED, financierUpdated.getAccreditationStatus());
        assertNotNull(financierUpdated.getUserIdentity().getNin());
        assertNotNull(financierUpdated.getUserIdentity().getTaxId());
//        assertNotNull(financierUpdated.getUserIdentity().getAddress());
        assertNotNull(financierUpdated.getUserIdentity().getBankDetail());
        assertNotNull(financierUpdated.getInheritanceOrGift());
        assertEquals(financierUpdated.getInheritanceOrGift(), financierWithKycRequest.getInheritanceOrGift());
        assertNotNull(financierUpdated.getPercentageOwnershipOrShare());
        assertEquals(financierUpdated.getOccupation(), financierWithKycRequest.getOccupation());
        assertNotNull(financierUpdated.getCountryOfIncorporation());

    }

    @Test
    @Order(6)
    void viewAllFinanciers(){
        Page<Financier> financiersPage = null;
        try {
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
    @Order(7)
    void findFinancierById() {
        Financier foundFinancier = null;
        try {
            foundFinancier = financierUseCase.viewFinancierDetail(individualUserIdentityId, individualFinancierId);
            log.info("-----> financier -----> " + foundFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals(individualFinancierId, foundFinancier.getId());
        assertNotNull(foundFinancier.getUserIdentity());
        assertNotNull(foundFinancier.getUserIdentity());
//        assertNotNull(foundFinancier.getUserIdentity().getAddress());
        assertNotNull(foundFinancier.getUserIdentity().getBankDetail());
        assertNotNull(foundFinancier.getInheritanceOrGift());
        assertNotNull(foundFinancier.getPercentageOwnershipOrShare());
        assertNotNull(foundFinancier.getCountryOfIncorporation());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ndnifeif"})
    void findFinancierByInvalidId(String invalidId) {
        assertThrows(MeedlException.class, ()-> financierUseCase.viewFinancierDetail(invalidId, individualFinancierId));
    }
    @Test
    @Order(8)
    public void viewAllFinancierInInvestmentVehicle() {
        Page<Financier> financiersPage = null;
        individualFinancier.setInvestmentVehicleId(investmentVehicleId);
        try {
            financiersPage = financierUseCase.viewAllFinancierInInvestmentVehicle(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        List<Financier> financiers = financiersPage.toList();
        assertFalse(financiers.isEmpty());
        assertEquals(individualFinancierId, financiers.get(0).getId());
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
    @Order(9)
    void viewAllFinancierInVehicleWithActivationStatus(){
        Page<Financier> financiersPage = null;
        try {
            financiersPage = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicleId, ActivationStatus.ACTIVE, pageRequest);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        List<Financier> financiers = financiersPage.toList();
        assertEquals(1, financiers.size());
        assertNotNull(financiers.get(0).getActivationStatus());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ervkdldd"})
    void viewAllFinancierInVehicleWithStatusAndInvalidVehicleId(String invalidId) {
        assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(invalidId, ActivationStatus.INVITED, pageRequest));
    }
    @Test
    void viewAllFinancierInVehicleWithVehicleIdAndInvalidStatus() {
        assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicleId, null, pageRequest));
    }
    @Test
    @Order(10)
    public void inviteIndividualFinancierToNewVehicle() {
        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForIndividualServiceTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        privateInvestmentVehicleId = investmentVehicle.getId();
        individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());
        individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());
        String response;
        try {
            response = financierUseCase.inviteFinancier(individualFinancierList, privateInvestmentVehicleId);
        } catch (MeedlException e) {
            log.error("Failed to invite with error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier has been added to investment vehicle", response);
        Page<Financier> financiers;
        try {
            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicle.getId(), pageRequest);
            deleteInvestmentVehicleFinancier(investmentVehicle.getId(), individualFinancierId);
            investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiers);
        assertFalse(financiers.isEmpty());
        assertEquals(individualFinancierId, financiers.getContent().get(0).getId());
        assertNotNull(individualFinancierId);

    }
    @Test
    @Order(11)
    public void inviteCooperateFinancierToNewVehicle() {

        UserIdentity cooperateUserIdentity = TestData.createTestUserIdentity("cooperateFinancierEmailtest@email.com", "ead0f7cb-5484-4bb8-b371-433850a9c367");
        Financier cooperateFinancier = buildCooperateFinancier(cooperateUserIdentity,  "NewVehicleCooperationTestCooperationService" );

        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForCooperateServiceTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
//        cooperateFinancier.setInvestmentVehicleId(investmentVehicle.getId());
        List<Financier> cooperateFinancierList = List.of(cooperateFinancier);

        String response;
        try {
            response = financierUseCase.inviteFinancier(cooperateFinancierList, investmentVehicle.getId());
        } catch (MeedlException e) {
            log.error("Failed to invite with error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier has been added to investment vehicle", response);
        Page<Financier> financiers;
        try {
            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicle.getId(), pageRequest);
            cooperateFinancier = financierOutputPort.findFinancierByUserId(cooperateUserIdentity.getId());
            deleteInvestmentVehicleFinancier(investmentVehicle.getId(), cooperateFinancier.getId());
            financierOutputPort.delete(cooperateFinancier.getId());
            deleteNotification(cooperateUserIdentity.getId());
            userIdentityOutputPort.deleteUserById(cooperateUserIdentity.getId());
            identityManagerOutputPort.deleteUser(cooperateUserIdentity);
            investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiers);
        assertFalse(financiers.isEmpty());
        assertNotNull(cooperateFinancier.getId());
        assertEquals(cooperateFinancier.getId(), financiers.getContent().get(0).getId());
        assertEquals(ActivationStatus.INVITED, financiers.getContent().get(0).getActivationStatus());
        assertEquals(AccreditationStatus.UNVERIFIED, financiers.getContent().get(0).getAccreditationStatus());

    }
    @Test
    public void inviteFinancierToNoneExistentInvestmentVehicle(){
        individualFinancier.setInvestmentVehicleId("61fb3beb-f200-4b16-ac58-c28d737b546c");
        assertThrows(MeedlException.class,()-> financierUseCase.inviteFinancier(individualFinancierList, investmentVehicleId));
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
        assertThrows(MeedlException.class,()-> financierUseCase.search(name, null, pageNumber, pageSize));
    }
    @Test
    @Order(12)
    void searchFinancierByFirstName()  {
        Page<Financier> foundFinanciers = null;
        try {
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getFirstName(), null, pageNumber, pageSize);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(13)
    void searchFinancierByLastName() {
        Page<Financier> foundFinanciers;
        try {

            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getLastName(),
                    null, individualFinancier.getPageNumber(), individualFinancier.getPageSize());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(14)
    void searchFinancierWithFirstNameBeforeLastName() {
        Page<Financier> foundFinanciers;
        try {
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getFirstName() +" "+ individualFinancier.getUserIdentity().getLastName()
            , null, individualFinancier.getPageNumber(), individualFinancier.getPageSize());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(15)
    void searchFinancierWithLastNameBeforeFirstName() {
        Page<Financier> foundFinanciers;
        try {
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getLastName() +" "+ individualFinancier.getUserIdentity().getFirstName()
                    , null, individualFinancier.getPageNumber(), individualFinancier.getPageSize());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(15)
    void searchFinancierWithEmail() {
        Page<Financier> foundFinanciers;
        try {
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getEmail()
                    , null, individualFinancier.getPageNumber(), individualFinancier.getPageSize());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(16)
    void searchFinancierInVehicle() {
        Page<Financier> foundFinanciers;
        try {
            foundFinanciers = financierUseCase.search(individualFinancier.getUserIdentity().getLastName()
                    , investmentVehicleId, individualFinancier.getPageNumber(), individualFinancier.getPageSize());
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
        assertThrows(MeedlException.class, ()->financierUseCase.viewInvestmentDetailOfFinancier(financierId, portfolioManagerId));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ijk"})
    void viewInvestmentDetailOfFinancierWithNullPortfolioManagerId(String pmId){
        assertThrows(MeedlException.class, ()->financierUseCase.viewInvestmentDetailOfFinancier(individualFinancierId, pmId));
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
    @Order(17)
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
    @Order(18)
    public void inviteCooperateFinancierToPlatform() {
        Financier foundFinancier;
        String inviteResponse;
        try {
            cooperateFinancier.setInvestmentVehicleId(null);
            inviteResponse = financierUseCase.inviteFinancier(cooperateFinancierList, null);
            log.info("Saved cooperate financier: {}", inviteResponse);
            cooperateUserIdentity = userIdentityOutputPort.findByEmail(cooperateFinancierEmail);
            cooperateUserIdentityId = cooperateUserIdentity.getId();
            foundFinancier = financierOutputPort.findFinancierByUserId(cooperateUserIdentityId);
            cooperateFinancierId = foundFinancier.getId();
        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals( foundFinancier.getFinancierType(), FinancierType.COOPERATE);
        assertNotNull(foundFinancier.getCooperation());
        assertNotNull(foundFinancier.getCooperation().getName());
        assertEquals( foundFinancier.getCooperation().getName(), cooperateFinancier.getCooperation().getName());
        assertNotNull(foundFinancier.getUserIdentity());
        assertEquals( foundFinancier.getUserIdentity().getId(), cooperateFinancier.getUserIdentity().getId());
        assertEquals( foundFinancier.getUserIdentity().getEmail(), cooperateFinancier.getUserIdentity().getEmail());
        assertNotNull(foundFinancier.getId());
        assertEquals(ActivationStatus.INVITED, foundFinancier.getActivationStatus());

        Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier;
        try {
            optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, cooperateFinancierId);
        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        assertTrue(optionalInvestmentVehicleFinancier.isEmpty());
    }
    @Test
    @Order(19)
    public void addMultipleExistingFinanciersToInvestmentVehicle() {
        List<Financier> financiers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            UserIdentity userIdentity = TestData.createTestUserIdentity(i+"unittestfinancier@email.com", "ead0f8cb-5487-4bb8-b271-313990a9c36"+i);
            Financier financier = TestData.buildFinancierIndividual(userIdentity);
            financiers.add(financier);
        }
        try {
            financierUseCase.inviteFinancier(financiers, null);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        List<Financier> invitedFinanciers = financiers.stream()
                .map(financier -> {
                    Financier  foundFinancier = null;
                    try {
                        foundFinancier = financierOutputPort.findFinancierByEmail(financier.getUserIdentity().getEmail());
                        foundFinancier.setInvestmentVehicleDesignation(Set.of(InvestmentVehicleDesignation.LEAD));
                        foundFinancier.getUserIdentity().setEmail(null);
                    } catch (MeedlException e) {
                        throw new RuntimeException(e);
                    }
                    return foundFinancier;
                }).toList();
        try {
            financierUseCase.inviteFinancier(invitedFinanciers, investmentVehicleId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        invitedFinanciers
                .forEach( financier ->
                        {
                            Optional<InvestmentVehicleFinancier> foundInvestmentVehicleFinancier;
                            try {
                                foundInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId,financier.getId() );
                            } catch (MeedlException e) {
                                throw new RuntimeException(e);
                            }
                            assertTrue(foundInvestmentVehicleFinancier.isPresent());
                            try {
                                investmentVehicleFinancierOutputPort.deleteInvestmentVehicleFinancier(foundInvestmentVehicleFinancier.get().getId());
                                financierOutputPort.delete(financier.getId());
                                deleteNotification(financier.getUserIdentity().getId());
                                userIdentityOutputPort.deleteUserById(financier.getUserIdentity().getId());
                                identityManagerOutputPort.deleteUser(financier.getUserIdentity());
                            } catch (MeedlException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

    }
    @Test
    @Order(20)
    public void inviteCooperateFinancierToNewVehicleWithAmountToInvest() {

        UserIdentity cooperateUserIdentity = TestData.createTestUserIdentity("cooperateFinancierEmailtestwithamount@email.com", "ead0f7cb-5484-4bb8-b371-433850a9c367");
        Financier cooperateFinancier = buildCooperateFinancier(cooperateUserIdentity,  "NewVehicleCooperationTestCooperationServiceWithAmountToInvest" );

        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForCooperateServiceTestWithFinancierAmountToInvest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        cooperateFinancier.setAmountToInvest(new BigDecimal("10000.00"));
        List<Financier> cooperateFinancierList = List.of(cooperateFinancier);

        String response;
        Financier foundFinancier;
        try {
            assertNotNull(investmentVehicle.getTotalAvailableAmount());
            BigDecimal initialAmount = investmentVehicle.getTotalAvailableAmount();
            assertEquals(new BigDecimal("4000.00"), initialAmount);

            response = financierUseCase.inviteFinancier(cooperateFinancierList, investmentVehicle.getId());

            InvestmentVehicle updatedInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicle.getId());
            BigDecimal currentAmount = updatedInvestmentVehicle.getTotalAvailableAmount();
            assertEquals(initialAmount.add(cooperateFinancier.getAmountToInvest()), currentAmount,
                    "The total available amount should be updated correctly");

            foundFinancier = financierOutputPort.findFinancierByEmail(cooperateFinancier.getUserIdentity().getEmail());

            Optional<InvestmentVehicleFinancier> investmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), foundFinancier.getId());
            assertTrue(investmentVehicleFinancier.isPresent());
            assertEquals(cooperateFinancier.getAmountToInvest(), investmentVehicleFinancier.get().getAmountInvested(),
                    "The amount to invest should be updated correctly");
        } catch (MeedlException e) {
            log.error("Failed to invite with error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier has been added to investment vehicle", response);
        Page<Financier> financiers;
        try {
            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicle.getId(), pageRequest);
            cooperateFinancier = foundFinancier;
            investmentVehicleFinancierOutputPort.deleteByInvestmentVehicleIdAndFinancierId(investmentVehicle.getId(), cooperateFinancier.getId());
            deleteInvestmentVehicleFinancier(investmentVehicle.getId(), cooperateFinancier.getId());
            financierOutputPort.delete(cooperateFinancier.getId());
            deleteNotification(cooperateUserIdentity.getId());
            userIdentityOutputPort.deleteUserById(cooperateUserIdentity.getId());
            identityManagerOutputPort.deleteUser(cooperateUserIdentity);
            investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiers);
        assertFalse(financiers.isEmpty());
        assertNotNull(cooperateFinancier.getId());
        assertEquals(cooperateFinancier.getId(), financiers.getContent().get(0).getId());
        assertEquals(ActivationStatus.INVITED, financiers.getContent().get(0).getActivationStatus());
        assertEquals(AccreditationStatus.UNVERIFIED, financiers.getContent().get(0).getAccreditationStatus());

    }

    @Test
    void viewInvestmentDetailWithNonExistingFinancierId(){
        String testFinancierId = "547391e5-19be-42d6-b725-f7df35138dfb";
        Exception exception = assertThrows(MeedlException.class, ()->financierOutputPort.findFinancierByFinancierId(testFinancierId));
        log.info("------->Exception message------------>"+exception.getMessage());
    }


    @AfterAll
    void tearDown() throws MeedlException {

        log.info("Started deleting data in financier service test." );
        deleteNotification(individualUserIdentityId);
        deleteInvestmentVehicleFinancier(investmentVehicleId, individualFinancierId);
        deleteInvestmentVehicleFinancier(privateInvestmentVehicleId, individualFinancierId);
        deleteInvestmentVehicleFinancier(publicInvestmentVehicleId, individualFinancierId);

        financierOutputPort.delete(individualFinancierId);
        identityManagerOutputPort.deleteUser(individualUserIdentity);
        userIdentityOutputPort.deleteUserById(individualUserIdentityId);
        userIdentityOutputPort.deleteUserById(portfolioManagerId);

        deleteNotification(individualUserIdentityId);
        deleteInvestmentVehicleFinancier(investmentVehicleId, individualFinancierId);

        financierOutputPort.delete(cooperateFinancierId);
        cooperateUserIdentity.setId(cooperateUserIdentityId);
        identityManagerOutputPort.deleteUser(cooperateUserIdentity);
        userIdentityOutputPort.deleteUserById(cooperateUserIdentityId);

        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(publicInvestmentVehicleId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(privateInvestmentVehicleId);

        log.info("Test data deleted after test");
    }

    private void deleteInvestmentVehicleFinancier(String investmentVehicleId, String financierId) throws MeedlException {
        Optional<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByInvestmentVehicleIdAndFinancierId(investmentVehicleId, financierId);
        if (optionalInvestmentVehicleFinancier.isPresent()) {
            log.info("Deleting existing test data investment vehicle financier {}", optionalInvestmentVehicleFinancier.get().getId());
            investmentVehicleFinancierOutputPort.deleteInvestmentVehicleFinancier(optionalInvestmentVehicleFinancier.get().getId());
        }else {
            log.warn("Unable to find or delete test data for investment vehicle financier");
        }
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

