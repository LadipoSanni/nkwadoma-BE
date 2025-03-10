package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentVehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentVehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.investmentVehicle.InvestmentVehicleStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentVehicle.InvestmentVehicle;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class FinancierAdapterTest {
    @Autowired
    private FinancierOutputPort financierOutputPort;
    private Financier financier;
    private UserIdentity userIdentity;
    private String financierId;
    private InvestmentVehicle investmentVehicle;
    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;

    @BeforeEach
    void setUp(){
        userIdentity = TestData.createTestUserIdentity("financieremailadaptertest@mail.com");
        userIdentity.setRole(IdentityRole.FINANCIER);
        try {
            userIdentity = userIdentityOutputPort.save(userIdentity);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        financier = TestData.buildFinancierIndividual(userIdentity);
        investmentVehicle = TestData.buildInvestmentVehicle("FinancierVehicleForTest");
        investmentVehicle = createInvestmentVehicle(investmentVehicle);
        financier.setInvestmentVehicleId(investmentVehicle.getId());

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
    public void saveFinancier() {
        Financier response;
        try {
            response = financierOutputPort.saveFinancier(financier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(response);
        assertNotNull(response.getIndividual());
        assertNotNull(response.getId());
        assertEquals(financier.getIndividual().getId(), response.getIndividual().getId());
        financierId = response.getId();
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidFirstName(String name)  {
        userIdentity.setFirstName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinancierWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinancierWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void inviteFinancierWithInvalidCreatedBy(String invitedBy){
        financier.setInvitedBy(invitedBy);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
    }
    @Test
    public void inviteFinancierWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class,()-> financierOutputPort.saveFinancier(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidFirstName(String name){
        userIdentity.setFirstName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE})
    public void inviteFinanciersWithInvalidLastName(String name){
        userIdentity.setLastName(name);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt", "ead0f7cb-5483-4bb8-b271-813970a9c368"})
    public void inviteFinanciersWithInvalidEmail(String email){
        userIdentity.setEmail(email);
        financier.setIndividual(userIdentity);
        assertThrows( MeedlException.class,()-> financierOutputPort.saveFinancier(financier));
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
            financiersPage = financierOutputPort.viewAllFinancier(financier);
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
        assertThrows(MeedlException.class,()-> financierOutputPort.search(name));
    }
    @Test
    @Order(4)
    void searchFinancierByFirstName()  {
        List<Financier> foundFinanciers = null;
        try {
            foundFinanciers = financierOutputPort.search(financier.getIndividual().getFirstName());
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

            foundFinanciers = financierOutputPort.search(financier.getIndividual().getLastName());
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
            foundFinanciers = financierOutputPort.search(financier.getIndividual().getFirstName() +" "+ financier.getIndividual().getLastName());
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
            foundFinanciers = financierOutputPort.search(financier.getIndividual().getLastName() +" "+ financier.getIndividual().getFirstName());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(foundFinanciers);
        assertFalse(foundFinanciers.isEmpty());
        assertNotNull(foundFinanciers.get(0));
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
//    @AfterAll
    void tearDown() throws MeedlException {
        Optional <UserIdentity> foundUser = identityManagerOutputPort.getUserByEmail(userIdentity.getEmail());
        if (foundUser.isPresent()) {
            identityManagerOutputPort.deleteUser(foundUser.get());
            userIdentityOutputPort.deleteUserById(foundUser.get().getId());
            log.info("Test user deleted after test");
        }
        investmentVehicleOutputPort.deleteInvestmentVehicle(investmentVehicle.getId());
        log.info("Test investment vehicle deleted after test");
    }
}