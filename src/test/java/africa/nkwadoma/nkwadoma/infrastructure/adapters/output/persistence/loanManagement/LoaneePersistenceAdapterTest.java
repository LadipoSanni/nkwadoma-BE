package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanBreakDownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoaneePersistenceAdapterTest {
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    private Loanee firstLoanee ;
    private Loanee anotherLoanee ;
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private String secondId = "7bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private String loaneeId;
    private String secondLoaneeId;
    private String cohortId;
    private int pageSize = 2;
    private int pageNumber = 0;
    private LoaneeLoanDetail loaneeLoanDetail;
    private LoaneeLoanDetail secondLoaneeLoanDetail;


    @Autowired
    private LoaneeRepository loaneeRepository;
    private UserIdentity userIdentity;
    private UserIdentity anotherUser;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private String userId;
    private String loaneeLoanDetailId;





    @BeforeAll
    void setUpUserIdentity(){
        userIdentity = UserIdentity.builder().id(id).email("lekan@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        anotherUser = UserIdentity.builder().email("lekan1@gmail.com").firstName("leke").lastName("ayo")
                .createdBy(secondId).role(IdentityRole.LOANEE).build();
        loaneeLoanDetail = LoaneeLoanDetail.builder()
                .initialDeposit(BigDecimal.valueOf(200)).build();
        secondLoaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).build();
        try {
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);
            anotherUser = identityManagerOutputPort.createUser(anotherUser);
            anotherUser = identityOutputPort.save(anotherUser);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
            secondLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(secondLoaneeLoanDetail);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }

    }


    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setCohortId(id);
        firstLoanee.setCreatedBy(id);
        firstLoanee.setUserIdentity(userIdentity);
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);

        anotherLoanee = new Loanee();
        anotherLoanee.setCohortId(id);
        anotherLoanee.setCreatedBy(id);
        anotherLoanee.setUserIdentity(anotherUser);
        anotherLoanee.setLoaneeLoanDetail(secondLoaneeLoanDetail);

    }

    @Test
    void saveNullLoanee(){
        assertThrows(MeedlException.class , () -> loaneeOutputPort.save(null));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyFistName(String firstName){
        firstLoanee.getUserIdentity().setFirstName(firstName);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyLastName(String lastName){
        firstLoanee.getUserIdentity().setLastName(lastName);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));

    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyEmail(String email){
        firstLoanee.getUserIdentity().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveNullEmail(){
        firstLoanee.getUserIdentity().setEmail(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }
    @Test
    void saveNullFirstName(){
        firstLoanee.getUserIdentity().setFirstName(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveNullLastName(){
        firstLoanee.getUserIdentity().setLastName(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void saveLoaneeWithEmptyUserId(String userId){
        firstLoanee.setCreatedBy(userId);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void saveLoaneeWithEmptyCohortId(String cohortId){
        firstLoanee.setCohortId(cohortId);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullCohortId(){
        firstLoanee.setCohortId(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }
    @Test
    void saveLoaneeWithNullMeedleUserId(){
        firstLoanee.setCreatedBy(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }


    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void saveLoaneeWithEmptyEmail(String email){
        firstLoanee.getUserIdentity().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings = {"qudussgsg", "25355366363"})
    void saveLoaneeWithInvalidEmail(String email){
        firstLoanee.getUserIdentity().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullEmail(){
        firstLoanee.getUserIdentity().setEmail(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }


    @Order(1)
    @Test
    void saveLoanee(){
        Loanee loanee = new Loanee();
        firstLoanee.setFullName(userIdentity.getFirstName().concat(userIdentity.getLastName()));
        try {
            UserIdentity savedUserIdentity = identityOutputPort.save(firstLoanee.getUserIdentity());
            firstLoanee.setUserIdentity(savedUserIdentity);
            LoaneeLoanDetail savedLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(firstLoanee.getLoaneeLoanDetail());
            firstLoanee.setLoaneeLoanDetail(savedLoaneeLoanDetail);
            loanee = loaneeOutputPort.save(firstLoanee);
            loaneeId = loanee.getId();
            userId = savedUserIdentity.getId();
            loaneeLoanDetailId = savedLoaneeLoanDetail.getId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getUserIdentity().getFirstName(),firstLoanee.getUserIdentity().getFirstName());
        assertEquals(loanee.getCohortId(),firstLoanee.getCohortId());
    }

    @Order(2)
    @Test
    void saveAnotherLoanee(){
        Loanee loanee = new Loanee();
        anotherLoanee.setFullName(anotherUser.getFirstName().concat(anotherUser.getLastName()));
        try{
            loanee = loaneeOutputPort.save(anotherLoanee);
            secondLoaneeId = loanee.getId();
            cohortId = loanee.getCohortId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getUserIdentity().getFirstName(),anotherLoanee.getUserIdentity().getFirstName());
        assertEquals(loanee.getCohortId(),anotherLoanee.getCohortId());
    }

    @Order(3)
    @Test
    void findAllLoanee(){
        try {
            Page<Loanee> loanees = loaneeOutputPort.findAllLoaneeByCohortId(cohortId,pageSize,pageNumber);
            assertEquals(2,loanees.toList().size());
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }

    }
    @Order(4)
    @Test
    void findLoanee(){
        Loanee loanee = new Loanee();
        log.info("loaneeId = {}",loaneeId);
        try {
            loanee = loaneeOutputPort.findLoaneeById(loaneeId);
        } catch (MeedlException exception) {
            log.error(exception.getMessage());
        }
        log.info("loanee firstname {}",loanee.getUserIdentity().getFirstName());
        assertEquals(firstLoanee.getUserIdentity().getFirstName(),loanee.getUserIdentity().getFirstName());
        assertEquals(firstLoanee.getCohortId(),loanee.getCohortId());
    }

    @Test
    void findLoaneeWithNullId(){
        assertThrows(MeedlException.class,()-> loaneeOutputPort.findLoaneeById(null));
    }

    @Order(5)
    @Test
    void searchLoanee(){
        List<Loanee> loanees = new ArrayList<>();
        try{
            loanees = loaneeOutputPort.searchForLoaneeInCohort("le",cohortId);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertEquals(2,loanees.size());
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        identityManagerOutputPort.deleteUser(userIdentity);
        loaneeRepository.deleteById(loaneeId);
        loaneeRepository.deleteById(secondLoaneeId);
        identityOutputPort.deleteUserById(userIdentity.getId());
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);
        identityOutputPort.deleteUserById(anotherUser.getId());
        identityManagerOutputPort.deleteUser(anotherUser);
    }
}
