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
    private UserIdentity userIdentity;
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
    private final String userEmail = "financieremailadaptertest@mail.com";


    @BeforeAll
    void setUp(){
        bankDetail = TestData.buildBankDetail();
        userIdentity = savedUserToDb();
        saveCooperation(userIdentity);
        investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());
        nextOfKin = TestData.createNextOfKinData(individualFinancier.getIndividual());
    }

    private void saveCooperation(UserIdentity userIdentity) {
        log.info("Saved user identity for cooperation {}", userIdentity);
        Cooperation cooperation = TestData.buildCooperation("FaradeTestCooperation" , userIdentity);
        try {
            cooperation = cooperationOutputPort.save(cooperation);
        } catch (MeedlException e) {
            log.error("Failed to save in test ", e);
            throw new RuntimeException(e);
        }
        cooperateFinancier = TestData.buildCooperateFinancier(cooperation);
        cooperateFinancierId = cooperateFinancier.getId();
    }

    private UserIdentity savedUserToDb() {
        userIdentity = TestData.createTestUserIdentity(userEmail, "ead0f7cb-5483-4bb8-b271-813660a4c368");
        userIdentity.setRole(IdentityRole.FINANCIER);
        userIdentity.setLastName("financierService");
        try {
            userIdentity = userIdentityOutputPort.save(userIdentity);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        individualFinancier = TestData.buildFinancierIndividual(userIdentity);
        return userIdentity;
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
        userIdentity.setId(response.getIndividual().getId());
    }
    @Test
//    @Order(1)
    public void saveCooperateFinancier() {
        Financier response;
        try {
            response = financierOutputPort.save(cooperateFinancier);
        } catch (MeedlException e) {
            log.error("",e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertNotNull(response.getCooperation());
        assertNotNull(response.getCooperation().getUserIdentity());
        assertEquals( response.getIndividual().getId(), cooperateFinancier.getCooperation().getUserIdentity().getId());
        assertEquals( response.getFinancierType(), FinancierType.COOPERATE);
        assertNull(response.getIndividual());
        assertNotNull(response.getId());
//        financierId = response.getId();
//        userIdentity.setId(response.getIndividual().getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        userIdentity.setFirstName(name);
        individualFinancier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        individualFinancier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        individualFinancier.setIndividual(userIdentity);
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
        userIdentity.setFirstName(name);
        individualFinancier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        individualFinancier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        individualFinancier.setIndividual(userIdentity);
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
            foundFinancier = financierOutputPort.findFinancierByUserId(userIdentity.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertNotNull(foundFinancier.getIndividual());
        assertEquals(financierId, foundFinancier.getId());
        assertEquals(userIdentity.getId(), foundFinancier.getIndividual().getId());
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
        assertEquals(foundFinanciers.get(0).getIndividual().getId(), userIdentity.getId());
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
            userIdentity.setBankDetail(savedBankDetail);
            userIdentity.setNextOfKin(savedNextOfKin);
            userIdentity.setTaxId("48373748");
            userIdentity.setNin("79827947923898");
            userIdentity.setAddress("the place");
            userIdentity = userIdentityOutputPort.save(userIdentity);
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
        try {
            UserIdentity foundUser = userIdentityOutputPort.findByEmail(userEmail);
            userIdentityOutputPort.deleteUserById(foundUser.getId());
            log.info("Test user deleted after test");
        }catch (MeedlException e) {
            log.error("Error finding and deleting user in financier adapter test {} ---- user email {}", e.getMessage(), userEmail, e);
            throw new MeedlException(e);
        }

        log.info("Deleting other test data such as bank, next of kin and investment vehicle");
        bankDetailOutputPort.deleteById(bankDetailId);
        nextOfKinOutputPort.deleteNextOfKin(nextOfKinId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        log.info("Test investment vehicle deleted after test");
    }
}