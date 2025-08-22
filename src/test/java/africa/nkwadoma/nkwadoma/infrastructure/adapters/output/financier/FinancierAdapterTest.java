package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;

import africa.nkwadoma.nkwadoma.application.ports.output.bankdetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.NextOfKinOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentvehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.bankdetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.loan.NextOfKin;
import africa.nkwadoma.nkwadoma.testUtilities.TestUtils;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private BankDetail bankDetail;
    private NextOfKin nextOfKin;

    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Autowired
    private NextOfKinOutputPort nextOfKinOutputPort;
    @Autowired
    private BankDetailOutputPort bankDetailOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    private final String userEmail = TestUtils.generateEmail("financierindividualemailadapterstest", 5);

    @BeforeAll
    void setUp(){
        bankDetail = TestData.buildBankDetail();
        individualUserIdentity = TestData.createTestUserIdentity(userEmail, "ead0f7cb-5483-4bb8-b271-813660a4c368");
        individualUserIdentity.setRole(IdentityRole.FINANCIER);

        individualUserIdentity = savedUserToDb(userEmail, "ead0f7cb-5483-4bb8-b271-813660a4c368");
        individualFinancier = TestData.buildFinancierIndividual(individualUserIdentity);
        individualFinancier.setIdentity(individualUserIdentity.getId());

        investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        individualFinancier.setInvestmentVehicleId(investmentVehicle.getId());

        nextOfKin = TestData.createNextOfKinData(individualFinancier.getUserIdentity());
    }

    private UserIdentity savedUserToDb(String userEmail, String userId) {
        UserIdentity individualUserIdentity = TestData.createTestUserIdentity(userEmail, userId);
        individualUserIdentity.setRole(IdentityRole.FINANCIER);
        individualUserIdentity.setLastName("financierAdapterTest");
        try {
            individualUserIdentity = userIdentityOutputPort.save(individualUserIdentity);
        } catch (MeedlException e) {
            log.info("Error saving user identity for financier.",e);
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
        assertNotNull(response.getId());
        assertEquals(individualFinancier.getUserIdentity().getId(), response.getIdentity());
        assertEquals(individualFinancier.getUserIdentity().getId(), response.getIdentity());
        financierId = response.getId();
        individualUserIdentity.setId(response.getIdentity());
        individualUserIdentity.setId(response.getIdentity());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        individualUserIdentity.setFirstName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        individualFinancier.getUserIdentity().setCreatedBy(invitedBy);
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
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        individualUserIdentity.setLastName(name);
        individualFinancier.setUserIdentity(individualUserIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.save(individualFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        individualUserIdentity.setEmail(email);
        individualFinancier.setUserIdentity(individualUserIdentity);
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
        assertEquals(financierId, foundFinancier.getId());
        assertEquals(individualUserIdentity.getId(), foundFinancier.getIdentity());
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
    @Order(4)
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
        individualFinancier.setInvestmentVehicleId(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.search(name, individualFinancier));
    }
    @Test
    @Order(5)
    void searchFinancierByFirstName()  {
        Page<Financier> foundFinanciers = null;
        individualFinancier.setInvestmentVehicleId(null);
        try {
            foundFinanciers = financierOutputPort.search(individualFinancier.getUserIdentity().getFirstName(), individualFinancier);

        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(6)
    void searchFinancierByLastName() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierOutputPort.search(individualFinancier.getUserIdentity().getLastName(), individualFinancier);

        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(7)
    void searchFinancierWithFirstNameBeforeLastName() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierOutputPort
                    .search(individualFinancier.getUserIdentity().getFirstName() +" "+
                                    individualFinancier.getUserIdentity().getLastName(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.getContent().get(0));
    }
    @Test
    @Order(8)
    void searchFinancierWithLastNameBeforeFirstName() {
        Page<Financier> foundFinanciers;
        try {
            individualFinancier.setInvestmentVehicleId(null);
            foundFinanciers = financierOutputPort.search(individualFinancier.getUserIdentity().getLastName() +" "+
                            individualFinancier.getUserIdentity().getFirstName(), individualFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        log.info("Financiers found in search test {}", foundFinanciers.getContent().size());
        assertNotNull(foundFinanciers.getContent().get(0));

    }

    @Test
    void completeIndividualKycWithoutFinancierObject(){
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(null));
    }

    @Test
    void completeIndividualKycWithoutAccountNumber(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankNumber(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountName(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankName(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberGreaterThanFifteen(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().getBankDetail().setBankNumber("1234567890111213");
        assertThrows(MeedlException.class, ()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithoutTaxId(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().setTaxId(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutNin(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.getUserIdentity().setNin(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithNullUser(){
        Financier financierWithKycRequest = TestData.completeKycRequest(individualFinancier, bankDetail);
        financierWithKycRequest.setUserIdentity(null);
        assertThrows(MeedlException.class,()-> financierOutputPort.completeKyc(financierWithKycRequest));
    }
//    @Test
//    @Order(9)
//    void completeKycIndividual() {
//        Financier financierUpdated = null;
//        Financier foundFinancier = null;
//        try {
//            foundFinancier = financierOutputPort.findFinancierByFinancierId(financierId);
//            assertNotNull(foundFinancier.getIdentity());
//            assertEquals(AccreditationStatus.UNVERIFIED, foundFinancier.getAccreditationStatus());
//            log.info("financier found accreditation status  -------------> {}", foundFinancier.getAccreditationStatus());
//            assertNull(foundFinancier.getUserIdentity().getNextOfKin());
//
//            NextOfKin savedNextOfKin = nextOfKinOutputPort.save(nextOfKin);
//            nextOfKinId = savedNextOfKin.getId();
//            BankDetail savedBankDetail = bankDetailOutputPort.save(bankDetail);
//            bankDetailId = savedBankDetail.getId();
//            individualUserIdentity.setBankDetail(savedBankDetail);
//            individualUserIdentity.setNextOfKin(savedNextOfKin);
//            individualUserIdentity.setTaxId("48373748");
//            individualUserIdentity.setNin("79827947923898");
//            individualUserIdentity.setAddress("the place");
//            individualUserIdentity = userIdentityOutputPort.save(individualUserIdentity);
//            Financier financierWithKycRequest = TestData.completeKycRequest(foundFinancier, savedBankDetail);
//
//            financierUpdated = financierOutputPort.completeKyc(financierWithKycRequest);
//            foundFinancier = financierOutputPort.findFinancierByFinancierId(financierId);
//            log.info("financier updated accreditation status -------------> {}", financierUpdated.getAccreditationStatus());
//
//        } catch (MeedlException e) {
//            log.info("===================> {}", e.getMessage(), e);
//        }
//        assertNotNull(financierUpdated);
//        assertEquals(AccreditationStatus.VERIFIED, financierUpdated.getAccreditationStatus());
//        assertNotNull(financierUpdated.getUserIdentity().getNin());
//        assertNotNull(financierUpdated.getUserIdentity().getTaxId());
//        assertNotNull(financierUpdated.getUserIdentity().getAddress());
//        assertEquals(AccreditationStatus.VERIFIED, foundFinancier.getAccreditationStatus());
//        assertNotNull(foundFinancier.getUserIdentity());
//        assertNotNull(foundFinancier.getUserIdentity().getNextOfKin());
//        assertNotNull(foundFinancier.getUserIdentity().getBankDetail());
//    }
//


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

        UserIdentity foundUser = userIdentityOutputPort.findByEmail(userEmail);
        userIdentityOutputPort.deleteUserById(foundUser.getId());
        log.info("Test user deleted after test");

        log.info("Deleting other test data such as bank, next of kin and investment vehicle");
//        bankDetailOutputPort.deleteById(bankDetailId);
//        nextOfKinOutputPort.deleteNextOfKin(nextOfKinId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        log.info("Test investment vehicle deleted after test");
    }
}