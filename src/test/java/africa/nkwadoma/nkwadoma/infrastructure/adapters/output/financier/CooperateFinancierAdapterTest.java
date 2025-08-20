package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.financier;


import africa.nkwadoma.nkwadoma.application.ports.output.financier.CooperateFinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.financier.FinancierOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.investmentvehicle.CooperationOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.ActivationStatus;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.financier.CooperateFinancier;
import africa.nkwadoma.nkwadoma.domain.model.financier.Financier;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.investmentvehicle.Cooperation;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CooperateFinancierAdapterTest {


    @Autowired
    private CooperateFinancierOutputPort cooperateFinancierOutputPort;
    private CooperateFinancier cooperateFinancier;
    private CooperateFinancier superAdmincooperateFinancier;
    private Financier financier;
    private Financier superAdminFInancier;
    private UserIdentity cooperateAdmin;
    private Cooperation cooperate;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private FinancierOutputPort financierOutputPort;
    @Autowired
    private CooperationOutputPort cooperationOutputPort;
    private String superAdminCooperateFinancierID;
    private String adminCooperateFinancierId;
    private UserIdentity superAdminIdentity;


    @BeforeAll
    void setUp() throws MeedlException {
        superAdminIdentity = TestData.createTestUserIdentity("creator@grr.la");
        superAdminIdentity.setRole(IdentityRole.COOPERATE_FINANCIER_SUPER_ADMIN);
        superAdminIdentity =  userIdentityOutputPort.save(superAdminIdentity);
        superAdminFInancier = TestData.buildFinancierIndividual(superAdminIdentity);
        superAdminFInancier = financierOutputPort.save(superAdminFInancier);
        cooperate = TestData.buildCooperation("NepoBABY","nepobaby@grr.la");
        cooperate = cooperationOutputPort.save(cooperate);

        cooperateAdmin = TestData.createTestUserIdentity("financier@grr.la");
        cooperateAdmin.setCreatedBy(superAdminIdentity.getId());
        cooperateAdmin.setRole(IdentityRole.COOPERATE_FINANCIER_ADMIN);
        cooperateAdmin = userIdentityOutputPort.save(cooperateAdmin);
        financier = TestData.buildFinancierIndividual(cooperateAdmin);
        financier = financierOutputPort.save(financier);

        superAdmincooperateFinancier = TestData.buildCooperateFinancier(superAdminFInancier,cooperate);
        cooperateFinancier = TestData.buildCooperateFinancier(financier,cooperate);
    }

    @Test
    void saveNullCooperateFinancier() {
        assertThrows(MeedlException.class,()-> cooperateFinancierOutputPort.save(null));
    }

    @Test
    void saveCooperateFinancierWithNullCooperate() {
        superAdmincooperateFinancier.setCooperate(null);
        assertThrows(MeedlException.class,()-> cooperateFinancierOutputPort.save(superAdmincooperateFinancier));
    }

    @Test
    void saveCooperateFinancierWithNullFinancier() {
        superAdmincooperateFinancier.setFinancier(null);
        assertThrows(MeedlException.class, ()-> cooperateFinancierOutputPort.save(superAdmincooperateFinancier));
    }

    @Test
    void saveCooperateFinancierWithNullActivationStatus() {
        superAdmincooperateFinancier.setActivationStatus(null);
        assertThrows(MeedlException.class, ()-> cooperateFinancierOutputPort.save(superAdmincooperateFinancier));
    }

    @Order(1)
    @Test
    void saveCooperateFinancierCooperation() {
        CooperateFinancier savedCooperateFinancier = null;
        try{
            savedCooperateFinancier = cooperateFinancierOutputPort.save(superAdmincooperateFinancier);
            log.info("coperafe rinancier == {}",savedCooperateFinancier);
            superAdminCooperateFinancierID = savedCooperateFinancier.getId();
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(savedCooperateFinancier);
        assertEquals(savedCooperateFinancier.getActivationStatus(), superAdmincooperateFinancier.getActivationStatus());
    }

   @Order(2)
    @Test
    void saveCooperateFinancierAdmin() {
        CooperateFinancier savedCooperateFinancier = null;
        try{
            savedCooperateFinancier = cooperateFinancierOutputPort.save(cooperateFinancier);
            log.info("coperate financier == {}",savedCooperateFinancier);
            adminCooperateFinancierId = savedCooperateFinancier.getId();
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(savedCooperateFinancier);
        assertEquals(savedCooperateFinancier.getActivationStatus(), cooperateFinancier.getActivationStatus());
    }


    @Order(3)
    @Test
    void findCooperateFinancierByUserIdentityId() {
        CooperateFinancier foundCooperateFinancier = null;
        try {
            foundCooperateFinancier = cooperateFinancierOutputPort.findByUserId(superAdminIdentity.getId());
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(foundCooperateFinancier);
        assertEquals(foundCooperateFinancier.getId(), superAdminCooperateFinancierID);
    }


    @Order(4)
    @Test
    void findCooperateFinancierByUserId() {
        CooperateFinancier foundCooperateFinancier = null;
        try{
            foundCooperateFinancier = cooperateFinancierOutputPort.findCooperateFinancierByUserId(superAdminIdentity.getId());
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(foundCooperateFinancier);
        assertEquals(foundCooperateFinancier.getId(), superAdminCooperateFinancierID);
    }

    @Order(5)
    @Test
    void findCooperateFinancierSuperAdminByCooperateName() {
        CooperateFinancier foundCooperateFinancier = null;
        try{
            foundCooperateFinancier = cooperateFinancierOutputPort.findCooperateFinancierSuperAdminByCooperateName("NepoBABY");
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(foundCooperateFinancier);
        assertEquals(foundCooperateFinancier.getId(), superAdminCooperateFinancierID);
    }


    @Order(6)
    @Test
    void findAllFinancierInCooperationByCooperationIdAndNullActivationStatus(){
        Page<CooperateFinancier> cooperateFinanciers = Page.empty();
        financier.setPageNumber(0);
        financier.setPageSize(10);
        financier.setActivationStatus(null);
        try {
            cooperateFinanciers = cooperateFinancierOutputPort.findAllFinancierInCooperationByCooperationId(cooperate.getId(),
                    financier);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(cooperateFinanciers);
        assertEquals(1, cooperateFinanciers.getContent().size());
    }

    @Order(7)
    @Test
    void findAllFinancierInCooperationByCooperationIdAndActiveActivationStatus(){
        Page<CooperateFinancier> cooperateFinanciers = Page.empty();
        financier.setPageNumber(0);
        financier.setPageSize(10);
        financier.setActivationStatus(ActivationStatus.ACTIVE);
        try {
            cooperateFinanciers = cooperateFinancierOutputPort.findAllFinancierInCooperationByCooperationId(cooperate.getId(),
                    financier);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(cooperateFinanciers);
        assertEquals(1, cooperateFinanciers.getContent().size());
    }


    @Order(8)
    @Test
    void findAllFinancierInCooperationByCooperationIdPendingApprovalActiveActivationStatus(){
        Page<CooperateFinancier> cooperateFinanciers = Page.empty();
        financier.setPageNumber(0);
        financier.setPageSize(10);
        financier.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        try {
            cooperateFinanciers = cooperateFinancierOutputPort.findAllFinancierInCooperationByCooperationId(cooperate.getId(),
                    financier);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertEquals(0,cooperateFinanciers.getContent().size());
    }


    @Test
    void findAllFinancierInCooperationByNullCooperationId(){
        assertThrows(MeedlException.class,()-> cooperateFinancierOutputPort.findAllFinancierInCooperationByCooperationId(null,financier));
    }


    @Order(9)
    @Test
    void searchCooperationStaffByCooperationIdAndNameNullActivationStatus(){
        Page<CooperateFinancier> cooperateFinanciers = Page.empty();
        financier.setPageNumber(0);
        financier.setPageSize(10);
        financier.setActivationStatus(null);
        financier.setName("j");
        try {
            cooperateFinanciers = cooperateFinancierOutputPort.searchCooperationStaffByCooperationIdAndStaffName(cooperate.getId(),
                    financier);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(cooperateFinanciers);
        assertEquals(1, cooperateFinanciers.getContent().size());
    }

    @Order(10)
    @Test
    void searchCooperationStaffByCooperationIdAndActiveActivationStatus(){
        Page<CooperateFinancier> cooperateFinanciers = Page.empty();
        financier.setPageNumber(0);
        financier.setPageSize(10);
        financier.setActivationStatus(ActivationStatus.ACTIVE);
        financier.setName("j");
        try {
            cooperateFinanciers = cooperateFinancierOutputPort.findAllFinancierInCooperationByCooperationId(cooperate.getId(),
                    financier);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertNotNull(cooperateFinanciers);
        assertEquals(1, cooperateFinanciers.getContent().size());
    }


    @Order(11)
    @Test
    void searchCooperationStaffByCooperationIdPendingApprovalActiveActivationStatus(){
        Page<CooperateFinancier> cooperateFinanciers = Page.empty();
        financier.setPageNumber(0);
        financier.setPageSize(10);
        financier.setActivationStatus(ActivationStatus.PENDING_APPROVAL);
        try {
            cooperateFinanciers = cooperateFinancierOutputPort.findAllFinancierInCooperationByCooperationId(cooperate.getId(),
                    financier);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertEquals(0,cooperateFinanciers.getContent().size());
    }


    @AfterAll
    void tearDown() throws MeedlException {
        cooperateFinancierOutputPort.delete(superAdminCooperateFinancierID);
        financierOutputPort.delete(superAdminFInancier.getId());
        userIdentityOutputPort.deleteUserById(superAdminIdentity.getId());

        cooperateFinancierOutputPort.delete(adminCooperateFinancierId);
        cooperationOutputPort.deleteById(cooperate.getId());
        financierOutputPort.delete(financier.getId());
        userIdentityOutputPort.deleteUserById(cooperateAdmin.getId());
    }


}
