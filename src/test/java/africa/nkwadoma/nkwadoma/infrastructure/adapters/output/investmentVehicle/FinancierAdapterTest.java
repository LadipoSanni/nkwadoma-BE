package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.bankDetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.NextOfKinOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.FinancierType;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Cooperation;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FinancierAdapterTest {
    @Autowired
    private FinancierOutputPort financierOutputPort;
    private Financier individualFinancier;
    private Financier cooperateFinancier;
    private UserIdentity individualUserIdentity;
    private String financierId;
    private String nextOfKinId;
    private String bankDetailId;
    private String cooperateFinancierId;
    private InvestmentVehicle investmentVehicle;

    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Autowired
    private CooperationOutputPort cooperationOutputPort;
    @Autowired
    private NextOfKinOutputPort nextOfKinOutputPort;
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private BankDetail bankDetail;
    private NextOfKin nextOfKin;
    private UserIdentity cooperateUserIdentity;
    private final String userEmail = "financieremailadaptertest@mail.com";


    @BeforeAll
    void setUp(){
        bankDetail = TestData.buildBankDetail();
        individualUserIdentity = savedUserToDb(userEmail, "ead0f7cb-5483-4bb8-b271-813660a4c368");
        individualFinancier = TestData.buildFinancierIndividual(individualUserIdentity);
        cooperateUserIdentity = savedUserToDb("adpatercooperatefinanciertest@emial.com", "ead0f7cb-5473-4bb8-b271-71350a4c363");
        saveCooperation(cooperateUserIdentity);
        investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());
        nextOfKin = TestData.createNextOfKinData(individualFinancier.getIndividual());
    }

    private void saveCooperation(UserIdentity userIdentity) {
        log.info("Saved user identity for cooperation {}", userIdentity);
        Cooperation cooperation = TestData.buildCooperation("FaradeTestCooperationAdapter");

        try {
            cooperation = cooperationOutputPort.save(cooperation);
        } catch (MeedlException e) {
            log.error("Failed to save in test ", e);
            throw new RuntimeException(e);
        }
        cooperateFinancier = TestData.buildCooperateFinancier(cooperation, userIdentity);

    }

    private UserIdentity savedUserToDb(String userEmail, String userId) {
        UserIdentity individualUserIdentity = TestData.createTestUserIdentity(userEmail, userId);
        individualUserIdentity.setRole(IdentityRole.FINANCIER);
        individualUserIdentity.setLastName("financierAdapterTest");
        try {
            individualUserIdentity = userIdentityOutputPort.save(individualUserIdentity);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        return individualUserIdentity;
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
            throw new RuntimeException(e);
        }
        return investmentVehicle;
    }

    @Test
    @Order(1)
    public void saveIndividualFinancier() {
        Financier response;
        try {
            response = financierOutputPort.save(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertNotNull(response.getIndividual());
        assertNotNull(response.getId());
        assertEquals(individualFinancier.getIndividual().getId(), response.getIndividual().getId());
        financierId = response.getId();
        individualUserIdentity.setId(response.getIndividual().getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        individualUserIdentity.setFirstName(name);
        individualFinancier.setIndividual(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setIndividual(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setIndividual(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        individualFinancier.setInvitedBy(invitedBy);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierOutputPort.save(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        individualUserIdentity.setFirstName(name);
        individualFinancier.setIndividual(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setIndividual(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setIndividual(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @Test
    @Order(2)
    void findFinancierByFinancierId() {
        Financier foundFinancier = null;
        try {
            foundFinancier = financierOutputPort.findFinancierByFinancierId(financierId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals(financierId, foundFinancier.getId());
    }
    @Test
    @Order(3)
    void findFinancierByUserId() {
        Financier foundFinancier = null;
        try {
            foundFinancier = financierOutputPort.findFinancierByUserId(individualUserIdentity.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertNotNull(foundFinancier.getIndividual());
        assertEquals(financierId, foundFinancier.getId());
        assertEquals(individualUserIdentity.getId(), foundFinancier.getIndividual().getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ndnifeif"})
    void findFinancierByInvalidFinancierId(String invalidId) {
        assertThrows(MeedlException.class, ()-> financierOutputPort.findFinancierByFinancierId(invalidId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ndnifeif"})
    void findFinancierByInvalidUserId(String invalidId) {
        assertThrows(MeedlException.class, ()-> financierOutputPort.findFinancierByUserId(invalidId));
    }
    @Test
    @Order(3)
    void viewAllFinanciers(){
        Page<Financier> financiersPage = null;
        try {
            financiersPage = financierOutputPort.viewAllFinancier(individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        List<Financier> financiers = financiersPage.toList();
        assertEquals(1, financiers.size());
    }

    @Test
    @Order(4)
    void viewInvestmentDetailsOfFinancier(){
        Financier financier = null;

    }

    @Test
    void viewFinanciersWithNull(){
        assertThrows(MeedlException.class,()-> financierOutputPort.viewAllFinancier(null));
    }

    @Test
    public void inviteLoaneeToBecomeAFinancier(){

    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "skfnjk"})
    void deleteFinancierWithInvalidFinancierId(String invalidId){
        assertThrows(MeedlException.class,()-> financierOutputPort.delete(invalidId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    void findByInvalidName(String name){
        assertThrows(MeedlException.class,()-> financierOutputPort.search(name));
    }
    @Test
    @Order(4)
    void searchFinancierByFirstName()  {
        List<Financier> foundFinanciers = null;
        try {
            foundFinanciers = financierOutputPort.search(individualFinancier.getIndividual().getFirstName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @Test
    @Order(5)
    void searchFinancierByLastName() {
        List<Financier> foundFinanciers;
        try {

            foundFinanciers = financierOutputPort.search(individualFinancier.getIndividual().getLastName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @Test
    @Order(6)
    void searchFinancierWithFirstNameBeforeLastName() {
        List<Financier> foundFinanciers;
        try {
            foundFinanciers = financierOutputPort.search(individualFinancier.getIndividual().getFirstName() +" "+ individualFinancier.getIndividual().getLastName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @Test
    @Order(7)
    void searchFinancierWithLastNameBeforeFirstName() {
        List<Financier> foundFinanciers;
        try {
            foundFinanciers = financierOutputPort.search(individualFinancier.getIndividual().getLastName() +" "+ individualFinancier.getIndividual().getFirstName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
        assertNotNull(foundFinanciers.get(0).getIndividual());
        assertEquals(foundFinanciers.get(0).getIndividual().getId(), individualUserIdentity.getId());
    }

    @Test
    void completeIndividualKycWithoutFinancierObject(){
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(null));
    }
    @Test
    void completeIndividualKycWithoutBankDetail(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().setBankDetail(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountNumber(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().getBankDetail().setAccountNumber(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountName(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().getBankDetail().setAccountName(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberLessThanTen(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().getBankDetail().setAccountNumber("123456789");
        assertThrows(MeedlException.class, ()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberGreaterThanFifteen(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().getBankDetail().setAccountNumber("1234567890111213");
        assertThrows(MeedlException.class, ()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithoutTaxId(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().setTaxId(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutNin(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().setNin(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeKycWithoutNextOfKin(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.getIndividual().setNextOfKin(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithNullUser(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail, nextOfKin);
        financierWithKycRequest.setIndividual(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    @Order(8)
    void completeKycIndividual() {
        Financier financierUpdated = null;
        Financier foundFinancier = null;
        try {
            foundFinancier = financierOutputPort.findFinancierByFinancierId(financierId);
            assertNotNull(foundFinancier.getIndividual());
            assertEquals(AccreditationStatus.UNVERIFIED, foundFinancier.getAccreditationStatus());
            log.info("financier found accreditation status  -------------> {}", foundFinancier.getAccreditationStatus());
            assertNull(foundFinancier.getIndividual().getNextOfKin());

            NextOfKin savedNextOfKin = nextOfKinOutputPort.save(nextOfKin);
            nextOfKinId = savedNextOfKin.getId();
            BankDetail savedBankDetail = bankDetailOutputPort.save(bankDetail);
            bankDetailId = savedBankDetail.getId();
            individualUserIdentity.setBankDetail(savedBankDetail);
            individualUserIdentity.setNextOfKin(savedNextOfKin);
            individualUserIdentity.setTaxId("48373748");
            individualUserIdentity.setNin("79827947923898");
            individualUserIdentity.setAddress("the place");
            individualUserIdentity = userIdentityOutputPort.save(individualUserIdentity);
            Financier financierWithKycRequest = TestData.completeKycRequest(foundFinancier, savedBankDetail, savedNextOfKin);

            financierUpdated = financierOutputPort.completeKyc(financierWithKycRequest);
            foundFinancier = financierOutputPort.findFinancierByFinancierId(financierId);
            log.info("financier updated accreditation status -------------> {}", financierUpdated.getAccreditationStatus());

        } catch (MeedlException e) {
            log.info("===================> {}", e.getMessage(), e);
        }
        assertNotNull(financierUpdated);
        assertEquals(AccreditationStatus.VERIFIED, financierUpdated.getAccreditationStatus());
        assertNotNull(financierUpdated.getIndividual().getNin());
        assertNotNull(financierUpdated.getIndividual().getTaxId());
        assertNotNull(financierUpdated.getIndividual().getAddress());
        assertEquals(AccreditationStatus.VERIFIED, foundFinancier.getAccreditationStatus());
        assertNotNull(foundFinancier.getIndividual());
        assertNotNull(foundFinancier.getIndividual().getNextOfKin());
        assertNotNull(foundFinancier.getIndividual().getBankDetail());
    }
    @Test
    @Order(9)
    public void saveCooperateFinancier() {
        Financier response;
        try {
            response = financierOutputPort.save(cooperateFinancier);
            log.info("Saved cooperate financier: {}", response);
            cooperateFinancierId = response.getId();
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals( response.getFinancierType(), FinancierType.COOPERATE);
        assertNotNull(response.getCooperation());
        assertNotNull(response.getCooperation().getName());
        assertEquals( response.getCooperation().getName(), cooperateFinancier.getCooperation().getName());
        assertNotNull(response.getIndividual());
        assertEquals( response.getIndividual().getId(), cooperateFinancier.getIndividual().getId());
        assertEquals( response.getIndividual().getEmail(), cooperateFinancier.getIndividual().getEmail());
        assertNotNull(response.getId());
    }

    @Test
    @Order(10)
    public void deleteFinancier(){
        try {
            Financier financier = financierOutputPort.findFinancierByFinancierId(financierId);
            assertNotNull(financier);
            financierOutputPort.delete(financierId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertThrows(MeedlException.class, ()-> financierOutputPort.findFinancierByFinancierId(financierId));
    }

    @AfterAll
    void tearDown() throws MeedlException {
        financierOutputPort.delete(cooperateFinancierId);
        Cooperation foundCooperation = cooperationOutputPort.findByName(cooperateFinancier.getCooperation().getName());
        cooperationOutputPort.deleteById(foundCooperation.getId());
//        userIdentityOutputPort.deleteUserById(foundCooperation.getUserIdentity().getId());

        UserIdentity foundUser = userIdentityOutputPort.findByEmail(userEmail);
        userIdentityOutputPort.deleteUserById(foundUser.getId());
        log.info("Test user deleted after test");

        log.info("Deleting other test data such as bank, next of kin and investment vehicle");
        bankDetailOutputPort.deleteById(bankDetailId);
        nextOfKinOutputPort.deleteNextOfKin(nextOfKinId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        log.info("Test investment vehicle deleted after test");
    }
}