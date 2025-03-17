package africa.nkwadoma.nkwadoma.domain.service.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.input.investmentVehicle.FinancierUseCase;
import africa.nkwadoma.nkwadoma.application.ports.output.bankDetail.BankDetailOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.meedlNotification.MeedlNotificationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.AccreditationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleDesignation;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.MeedlNotification;
import africa.nkwadoma.nkwadoma.domain.model.bankDetail.BankDetail;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    private MeedlNotificationOutputPort meedlNotificationOutputPort;
    private final Pageable pageRequest = PageRequest.of(0, 10);
    private Financier financier;
    private UserIdentity userIdentity;
    private String userIdentityId;
    private String financierId;
    private BankDetail bankDetail;
    private String investmentVehicleId;
    int pageSize = 10 ;
    int pageNumber = 0 ;

    @BeforeAll
    void setUp(){
        bankDetail = TestData.buildBankDetail();
        userIdentity = TestData.createTestUserIdentity("financierservicetest2@mail.com");
        userIdentity.setRole(IdentityRole.FINANCIER);
        financier = TestData.buildFinancierIndividual(userIdentity);
        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForServiceTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        investmentVehicleId = investmentVehicle.getId();
        financier.setInvestmentVehicleId(investmentVehicleId);
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
    public void inviteFinancierThatDoesNotExistOnThePlatformToInvestmentVehicle() {
        String response;
        Financier foundFinancier;
        try {
            response = financierUseCase.inviteFinancier(financier);
            userIdentity = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
            userIdentityId = userIdentity.getId();
            foundFinancier = financierOutputPort.findFinancierByUserId(userIdentityId);
            financierId = foundFinancier.getId();
            log.info("Financier id for test user with id : {} is {}", userIdentityId, financierId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier added to investment vehicle", response);
        assertEquals(ActivationStatus.INVITED, foundFinancier.getActivationStatus());
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
        userIdentity.setFirstName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @Test
    public void assignDesignationToFinancierWrongly()  {
        Set<InvestmentVehicleDesignation> investmentVehicleDesignations = new HashSet<>();
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.LEAD);
        investmentVehicleDesignations.add(InvestmentVehicleDesignation.DONOR);
        financier.setInvestmentVehicleDesignation(investmentVehicleDesignations);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        financier.setInvitedBy(invitedBy);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierUseCase.inviteFinancier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        userIdentity.setFirstName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @Test
    void completeIndividualKycWithoutFinancierObject(){
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(null));
    }
    @Test
    void completeIndividualKycWithoutBankDetail(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.setBankDetail(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountNumber(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.getBankDetail().setAccountNumber(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutAccountName(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.getBankDetail().setAccountName(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberLessThanTen(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.getBankDetail().setAccountNumber("123456789");
        assertThrows(MeedlException.class, ()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithAccountNumberGreaterThanFifteen(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.getBankDetail().setAccountNumber("1234567890111213");
        assertThrows(MeedlException.class, ()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    void completeIndividualKycWithoutTaxId(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.setTaxId(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeIndividualKycWithoutNin(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.setNin(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }
    @Test
    void completeKycWithoutNextOfKin(){
        Financier financierWithKycRequest = TestData.completeKycRequest(financier, bankDetail);
        financierWithKycRequest.getIndividual().setNextOfKin(null);
        assertThrows(MeedlException.class,()-> financierUseCase.completeKyc(financierWithKycRequest));
    }

    @Test
    @Order(2)
    void completeKycIndividual() {
        Financier financierUpdated = null;
        try {
            Financier foundFinancier = financierUseCase.viewFinancierDetail(financierId);
            assertNotNull(foundFinancier.getIndividual());
            assertEquals(AccreditationStatus.UNVERIFIED ,foundFinancier.getAccreditationStatus());
            log.info("financier found accreditation status  -------------> {}", foundFinancier.getAccreditationStatus());
            assertNull(foundFinancier.getIndividual().getNextOfKin());
            Financier financierWithKycRequest = TestData.completeKycRequest(foundFinancier, bankDetail);
            financierUpdated = financierUseCase.completeKyc(financierWithKycRequest);
            log.info("financier updated accreditation status -------------> {}", financierUpdated.getAccreditationStatus());

        } catch (MeedlException e) {
            log.info("===================> " + e.getMessage());
        }
        assertNotNull(financierUpdated);
        assertEquals(AccreditationStatus.VERIFIED ,financierUpdated.getAccreditationStatus());
        assertNotNull(financierUpdated.getNin());
        assertNotNull(financierUpdated.getTaxId());
        assertNotNull(financierUpdated.getAddress());
        assertNotNull(financierUpdated.getBankDetail());
        assertNotNull(financierUpdated.getIndividual().getNextOfKin());

    }
    @Test
    @Order(3)
    void viewAllFinanciers(){
        Page<Financier> financiersPage = null;
        try {
            financiersPage = financierUseCase.viewAllFinancier(financier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        List<Financier> financiers = financiersPage.toList();
        assertFalse(financiers.isEmpty());
    }
    @Test
    @Order(4)
    void findFinancierById() {
        Financier foundFinancier = null;
        try {
            foundFinancier = financierUseCase.viewFinancierDetail(financierId);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinancier);
        assertEquals(financierId, foundFinancier.getId());
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ndnifeif"})
    void findFinancierByInvalidId(String invalidId) {
        assertThrows(MeedlException.class, ()-> financierUseCase.viewFinancierDetail(invalidId));
    }
    @Test
    @Order(5)
    public void viewAllFinancierInInvestmentVehicle() {
        Page<Financier> financiersPage = null;
        financier.setInvestmentVehicleId(investmentVehicleId);
        try {
            financiersPage = financierUseCase.viewAllFinancierInInvestmentVehicle(financier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        List<Financier> financiers = financiersPage.toList();
        assertFalse(financiers.isEmpty());
        assertEquals(financierId, financiers.get(0).getId());
    }
    @Test
    void viewAllFinancierInVehicleWithNull(){
        assertThrows(MeedlException.class,()-> financierUseCase.viewAllFinancierInInvestmentVehicle(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "hidhfj"})
    void viewAllFinanciersInInvestmentVehicleWithInvalidVehicleId(String invalidId) {
        financier.setInvestmentVehicleId(invalidId);
        assertThrows(MeedlException.class,()-> financierUseCase.viewAllFinancierInInvestmentVehicle(financier));
    }
    @Test
    void viewFinanciersWithNull(){
        assertThrows(MeedlException.class,()-> financierUseCase.viewAllFinancier(null));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidOrNonExistingInvestmentVehicleId(String investmentVehicleId){
        financier.setInvestmentVehicleId(investmentVehicleId);
        assertThrows( MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
    }
    @Test
    @Order(6)
    void viewAllFinancierInVehicleWithActivationStatus(){
        Page<Financier> financiersPage = null;
        try {
            financiersPage = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicleId, ActivationStatus.INVITED, pageRequest);
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
    @Order(7)
    public void inviteFinancierToNewVehicle() {
        InvestmentVehicle investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForServiceTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        financier.setInvestmentVehicleId(investmentVehicle.getId());
        String response;
        try {
            response = financierUseCase.inviteFinancier(financier);
        } catch (MeedlException e) {
            log.error("Failed to invite with error {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertEquals("Financier added to investment vehicle", response);
        Page<Financier> financiers;
        try {
            financiers = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicle.getId(), pageRequest);
            deleteInvestmentVehicleFinancier(investmentVehicle.getId(), financierId);
            investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiers);
        assertNotNull(financierId);
        assertFalse(financiers.isEmpty());
        assertEquals(financierId, financiers.getContent().get(0).getId());

    }
    @Test
    public void inviteFinancierToNoneExistentInvestmentVehicle(){
        financier.setInvestmentVehicleId("61fb3beb-f200-4b16-ac58-c28d737b546c");
        assertThrows(MeedlException.class,()-> financierUseCase.inviteFinancier(financier));
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
        assertThrows(MeedlException.class,()-> financierOutputPort.search(name));
    }
    @Test
    @Order(8)
    void searchFinancierByFirstName()  {
        List<Financier> foundFinanciers = null;
        try {
            foundFinanciers = financierUseCase.search(financier.getIndividual().getFirstName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @Test
    @Order(9)
    void searchFinancierByLastName() {
        List<Financier> foundFinanciers;
        try {

            foundFinanciers = financierUseCase.search(financier.getIndividual().getLastName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @Test
    @Order(10)
    void searchFinancierWithFirstNameBeforeLastName() {
        List<Financier> foundFinanciers;
        try {
            foundFinanciers = financierUseCase.search(financier.getIndividual().getFirstName() +" "+ financier.getIndividual().getLastName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @Test
    @Order(11)
    void searchFinancierWithLastNameBeforeFirstName() {
        List<Financier> foundFinanciers;
        try {
            foundFinanciers = financierUseCase.search(financier.getIndividual().getLastName() +" "+ financier.getIndividual().getFirstName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
    }
    @AfterAll
    void tearDown() throws MeedlException {
        log.info("Started deleting data in financier service test." );
        deleteNotification(userIdentityId);
        deleteInvestmentVehicleFinancier(investmentVehicleId, financierId);
        financierOutputPort.delete(financierId);
        identityManagerOutputPort.deleteUser(userIdentity);
        userIdentityOutputPort.deleteUserById(userIdentityId);
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicleId);
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

    private void deleteNotification(String userIdentityId) throws MeedlException {
        Page<MeedlNotification> meedlNotifications = meedlNotificationOutputPort.findAllNotificationBelongingToAUser(userIdentityId,pageSize,pageNumber);
        meedlNotifications.forEach(notification-> {
            try {
                meedlNotificationOutputPort.deleteNotification(notification.getId());
                log.info("Deleting notifications for test user with id {} and notification with id {}", userIdentityId, notification.getId());
            } catch (MeedlException e) {
                log.warn("Unable to delete notification for test user with id {}", userIdentityId);
                throw new RuntimeException(e);
            }
        });
    }

}

