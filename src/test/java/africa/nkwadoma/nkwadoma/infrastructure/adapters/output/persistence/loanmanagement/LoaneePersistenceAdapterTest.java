package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


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
    private int pageSize = 2;
    private int pageNumber = 0;


    @Autowired
    private LoaneeRepository loaneeRepository;
    private UserIdentity userIdentity;
    private UserIdentity anotherUser;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    private String userId;
    private String otherUserId;


    @BeforeAll
    void setUpUserIdentity(){
        userIdentity = UserIdentity.builder().id(id).email("lekan@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        try {
            Optional<UserIdentity> userByEmail = identityManagerOutputPort.getUserByEmail(userIdentity.getEmail());
            if (userByEmail.isPresent()) {
                identityManagerOutputPort.deleteUser(userByEmail.get());
            }
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);

        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }


    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setUserIdentity(userIdentity);

        anotherUser = UserIdentity.builder().email("lekan1@gmail.com").firstName("leke").lastName("ayo")
                .createdBy(secondId).role(IdentityRole.LOANEE).build();
        try {
            Optional<UserIdentity> otherUser = identityManagerOutputPort.getUserByEmail(anotherUser.getEmail());
            if (otherUser.isPresent()) {
                identityManagerOutputPort.deleteUser(otherUser.get());
            }
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        anotherLoanee = new Loanee();
        anotherLoanee.setUserIdentity(anotherUser);

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
    void saveLoaneeWithEmptyCohortId(String cohortId){
        firstLoanee.setCohortId(cohortId);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullCohortId(){
        firstLoanee.setCohortId(null);
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
        try {
            UserIdentity savedUserIdentity = identityOutputPort.save(firstLoanee.getUserIdentity());
            firstLoanee.setUserIdentity(savedUserIdentity);
            loanee = loaneeOutputPort.save(firstLoanee);
            loaneeId = loanee.getId();
            userId = savedUserIdentity.getId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getUserIdentity().getFirstName(),firstLoanee.getUserIdentity().getFirstName());
    }

    @Order(2)
    @Test
    void saveAnotherLoanee(){
        Loanee loanee = new Loanee();
        try{
            anotherUser = identityManagerOutputPort.createUser(anotherUser);
            anotherUser = identityOutputPort.save(anotherUser);
            anotherLoanee.setUserIdentity(anotherUser);
            loanee = loaneeOutputPort.save(anotherLoanee);
            secondLoaneeId = loanee.getId();
            otherUserId = anotherUser.getId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getUserIdentity().getFirstName(), anotherLoanee.getUserIdentity().getFirstName());
    }

    @Order(3)
    @Test
    void findAllLoanee(){
        // Todo
        // Would change to find all loanee and move find all loanee in a chort to another place

        Loanee cohortLoanee = Loanee.builder()
                .cohortId(firstLoanee.getCohortId())
                .loaneeStatus(null)
                .loanStatus(null)
                .build();
        Page<Loanee> loanees = Page.empty();
        try {
            loanees = loaneeOutputPort.findAllLoaneeByCohortId(cohortLoanee,pageSize,pageNumber);
            log.info("------> The loanees -----> {}", loanees.getContent());
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(0,loanees.toList().size());
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
    }

    @Test
    void findLoaneeWithNullId(){
        assertThrows(MeedlException.class,()-> loaneeOutputPort.findLoaneeById(null));
    }

    @Order(5)
    @Test
    void searchLoanee(){
        // Todo
        // Change search for loanee in platform and move search in a cohort to another place


        Page<Loanee> loanees = Page.empty();
        firstLoanee.setLoaneeName("le");
        try{
            loanees = loaneeOutputPort.searchForLoaneeInCohort(firstLoanee,pageSize,pageNumber);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertEquals(0,loanees.getContent().size());
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeRepository.deleteById(loaneeId);
        identityManagerOutputPort.deleteUser(userIdentity);
        identityOutputPort.deleteUserById(userId);
        loaneeRepository.deleteById(secondLoaneeId);
        Optional<UserIdentity> userByEmail = identityManagerOutputPort.getUserByEmail(anotherUser.getEmail());
        if(userByEmail.isPresent()){
            identityManagerOutputPort.deleteUser(userByEmail.get());
        }
        identityOutputPort.deleteUserById(otherUserId);
    }
}
