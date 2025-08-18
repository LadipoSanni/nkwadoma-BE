package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.investmentvehicle;

import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.InvestmentVehicleOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicle;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.InvestmentVehicleFinancier;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvestmentVehicleFinancierAdapterTest {
    @Autowired
    private InvestmentVehicleFinancierOutputPort investmentVehicleFinancierOutputPort;
    @Autowired
    private InvestmentVehicleOutputPort investmentVehicleOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private FinancierOutputPort financierOutputPort;

    private InvestmentVehicleFinancier investmentVehicleFinancier;
    private InvestmentVehicle investmentVehicle;
    private UserIdentity userIdentity;
     private String investmentVehicleId;
     private Financier financier;
    private String financierId;
    private final Pageable pageRequest = PageRequest.of(0, 10);
    @BeforeAll
    void setUp() {
        try {
            userIdentity = saveUserIdentity(TestData.createTestUserIdentity("InvestmentVehicleFinanciertest@notmail.com", "3f89a9e1-62a5-4b42-bff1-6c8b5f77c5e2"));
            log.info("Financier saved successfully for investment vehicle financier test. {}", userIdentity.getId());
            financier = financierOutputPort.save(TestData.buildFinancierIndividual(userIdentity));

            financierId = financier.getId();
        } catch (MeedlException e) {
            log.warn("Failed to create user on db {}", e.getMessage(), e);
        }
        investmentVehicle = saveInvestmentVehicle(TestData.buildInvestmentVehicle("InvestmentVehicleFinancierTest"));
        log.info("Successfully saved the vehicle for investment vehicle financier test");
        investmentVehicleFinancier = TestData.buildInvestmentVehicleFinancier(financier, investmentVehicle);
    }
    private UserIdentity saveUserIdentity(UserIdentity userIdentity) throws MeedlException {
        try {
            log.info("Finding user for testing investment vehicle financier {}", userIdentity.getId());
            return userIdentityOutputPort.findById(userIdentity.getId());
        } catch (MeedlException e) {
            log.info("Saving new user for testing investment vehicle financier.");
            return userIdentityOutputPort.save(userIdentity);
        }
    }
    private InvestmentVehicle saveInvestmentVehicle(InvestmentVehicle investmentVehicle) {
        if (StringUtils.isEmpty(investmentVehicleId)){
            try {
                investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
                investmentVehicleId = investmentVehicle.getId();

            } catch (MeedlException e) {
                throw new RuntimeException(e);
            }
            log.info("Investment vehicle saved -------------------------------- {}", investmentVehicle.getId());
            return investmentVehicle;
        }
        try {
            InvestmentVehicle foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            if (foundInvestmentVehicle == null){
                log.info("Investment vehicle NOT found  --------------------------------------------------{}", investmentVehicleId);
                investmentVehicle = investmentVehicleOutputPort.save(investmentVehicle);
                investmentVehicleId = investmentVehicle.getId();
                log.info("Investment vehicle saved -------------------------------- {}", investmentVehicle.getId());
            }else{
                investmentVehicle = foundInvestmentVehicle;
                investmentVehicleId = foundInvestmentVehicle.getId();
                log.info("Investment vehicle found  --------------------------------------------------{}", investmentVehicle.getName());
            }
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        return investmentVehicle;
    }


    @Test
    @Order(1)
    void save() {
        InvestmentVehicleFinancier savedInvestmentVehicleFinancier = null;
        try {
            savedInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        List<InvestmentVehicleFinancier> foundInvestmentVehicleFinancier = null;
        try {
            foundInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), financier.getId());
        } catch (MeedlException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
        assertFalse(foundInvestmentVehicleFinancier.isEmpty());
        assertNotNull(foundInvestmentVehicleFinancier.get(0));
        log.info("Financier investment vehicle found: " + foundInvestmentVehicleFinancier);
        assertNotNull(savedInvestmentVehicleFinancier);
        assertNotNull(savedInvestmentVehicleFinancier.getFinancier());
        assertNotNull(savedInvestmentVehicleFinancier.getInvestmentVehicle());
        assertNotNull(investmentVehicleFinancier.getFinancier());
        assertNotNull(investmentVehicleFinancier.getFinancier().getUserIdentity());
        assertEquals(investmentVehicleFinancier.getFinancier().getUserIdentity().getId(), financier.getUserIdentity().getId());
        assertEquals(investmentVehicleFinancier.getFinancier().getId(), financier.getId());
        assertEquals(investmentVehicleFinancier.getInvestmentVehicle().getId(), investmentVehicle.getId());

    }
    @Test
    void saveWithNullInvestmentVehicleFinancier() {
        assertThrows(MeedlException.class, () -> investmentVehicleFinancierOutputPort.save(null));
    }
    @Test
    void saveWithNullInvestmentVehicle() {
        investmentVehicleFinancier.setInvestmentVehicle(null);
        assertThrows(MeedlException.class, () -> investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier));
    }
    @Test
    void saveWithNullUserIdentity() {
        investmentVehicleFinancier.setFinancier(null);
        assertThrows(MeedlException.class, () -> investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier));
    }
    @Test
    void saveWithEmptyInvestmentVehicleId(){
        investmentVehicle.setId(null);
        investmentVehicleFinancier.setInvestmentVehicle(investmentVehicle);
        assertThrows(MeedlException.class, () -> investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier));
    }
    @Test
    void saveWithEmptyFinancierId(){
        financier.setId(null);
        investmentVehicleFinancier.setFinancier(financier);
        assertThrows(MeedlException.class, () -> investmentVehicleFinancierOutputPort.save(investmentVehicleFinancier));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void findByInvestmentVehicleIdAndInvalidFinancierId(String financierId){
        assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(),financierId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "gyfyt"})
    public void findByInvalidInvestmentVehicleIdAndValidFinancierId(String investmentVehicleId){
        assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.findByAll(investmentVehicleId,userIdentity.getId()));
    }
    @Test
    @Order(2)
    void findByInvestmentVehicleIdAndFinancierId(){
        List<InvestmentVehicleFinancier> optionalInvestmentVehicleFinancier = null;
        try {
            log.info("finding investment vehicle financier with vehicle id {} and financier id {}",investmentVehicle.getId(), userIdentity.getId());
            optionalInvestmentVehicleFinancier = investmentVehicleFinancierOutputPort.findByAll(investmentVehicle.getId(), financier.getId());
        } catch (MeedlException e) {
            log.error("Error while getting investment vehicle financier. {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
        assertFalse(optionalInvestmentVehicleFinancier.isEmpty());
        InvestmentVehicleFinancier foundInvestmentVehicleFinancier = optionalInvestmentVehicleFinancier.get(0);
        assertNotNull(foundInvestmentVehicleFinancier);
        assertEquals(foundInvestmentVehicleFinancier.getFinancier().getId(), financier.getId());
        assertEquals(foundInvestmentVehicleFinancier.getInvestmentVehicle().getId(), investmentVehicleId);
        assertEquals(investmentVehicleFinancier.getFinancier().getId(), financier.getId());
        assertEquals(investmentVehicleFinancier.getInvestmentVehicle().getId(), investmentVehicle.getId());
    }
    @Test
    @Order(3)
    void viewAllFinancierInVehicle(){
        Page<Financier> financiersPage = null;
        try {
            financiersPage = investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(investmentVehicleId,null,pageRequest);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(financiersPage);
        assertNotNull(financiersPage.getContent());
        List<Financier> financiers = financiersPage.toList();
        assertEquals(1, financiers.size());
    }
    @Test
    @Order(3)
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
    void viewAllFinancierInVehicleWithInvalidVehicleId(String invalidId) {
        assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(invalidId, null, pageRequest));
    }
        @ParameterizedTest
        @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "ervkdldd"})
        void viewAllFinancierInVehicleWithStatusAndInvalidVehicleId(String invalidId) {
            assertThrows(MeedlException.class, ()-> investmentVehicleFinancierOutputPort.viewAllFinancierInAnInvestmentVehicle(invalidId, ActivationStatus.INVITED, pageRequest));
        }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "invalid-id"})
    void deleteWithInvalidId(String invalidId){
        assertThrows(MeedlException.class, () -> investmentVehicleFinancierOutputPort.deleteInvestmentVehicleFinancier(invalidId));
    }
    @AfterAll
    void tearDown() {
        UserIdentity foundUser = null;
        InvestmentVehicleFinancier foundInvestmentVehicleFinancier = null;
        InvestmentVehicle foundInvestmentVehicle = null;

        try {
            foundUser = userIdentityOutputPort.findByEmail(userIdentity.getEmail());
            log.info("Found user to delete in test with id : {}", foundUser.getId());
            foundInvestmentVehicle = investmentVehicleOutputPort.findById(investmentVehicleId);
            log.info("Found investment vehicle for deleting with id {}", foundInvestmentVehicle.getId());
            List<InvestmentVehicleFinancier> investmentVehicleFinanciers = investmentVehicleFinancierOutputPort.findByAll(investmentVehicleId,financierId);
            if (!investmentVehicleFinanciers.isEmpty()) {
                foundInvestmentVehicleFinancier = investmentVehicleFinanciers.get(0);
                log.info("Found investment vehicle financier for deletion in test :{}", foundInvestmentVehicleFinancier.getId());
            }else {
                log.error("No investment vehicle financier found for deletion in test with user id : {}", foundUser.getId());
            }
        } catch (MeedlException e) {
            log.error("Error while deleting test data. {}",e.getMessage(), e);
        }
        try {
            if (foundInvestmentVehicle != null && foundInvestmentVehicleFinancier != null) {
                investmentVehicleFinancierOutputPort.deleteInvestmentVehicleFinancier(foundInvestmentVehicleFinancier.getId());
                investmentVehicleOutputPort.deleteInvestmentVehicle(foundInvestmentVehicle.getId());
            }else {
                log.info("Found investment vehicle to delete test data. {}, or investment vehicle financier is null {}", foundInvestmentVehicle, foundInvestmentVehicleFinancier);
            }
            if (foundUser != null) {
                financierOutputPort.delete(financierId);
                userIdentityOutputPort.deleteUserById(foundUser.getId());
            }else {
                log.info("Found user to delete test data {}",foundUser);
            }
            log.info("Test data deleted after test");
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
    }
}