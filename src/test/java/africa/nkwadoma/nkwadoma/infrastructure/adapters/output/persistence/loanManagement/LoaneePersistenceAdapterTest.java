package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.enums.constants.*;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.LoaneeStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.data.request.loanManagement.DeferProgramRequest;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.loan.LoaneeRepository;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private String secondLoaneeLoanDetailId;
    private String otherUserId;


    @BeforeAll
    void setUpUserIdentity(){
        userIdentity = UserIdentity.builder().id(id).email("lekan@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        loaneeLoanDetail = LoaneeLoanDetail.builder()
                .initialDeposit(BigDecimal.valueOf(200)).build();

        try {
            Optional<UserIdentity> userByEmail = identityManagerOutputPort.getUserByEmail(userIdentity.getEmail());
            if (userByEmail.isPresent()) {
                identityManagerOutputPort.deleteUser(userByEmail.get());
            }
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
    }


    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setCohortId(id);
        firstLoanee.setUserIdentity(userIdentity);
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);
        firstLoanee.setLoaneeStatus(LoaneeStatus.ADDED);

        secondLoaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).build();
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
        anotherLoanee.setCohortId(id);
        anotherLoanee.setUserIdentity(anotherUser);
        anotherLoanee.setLoaneeLoanDetail(secondLoaneeLoanDetail);
        anotherLoanee.setLoaneeStatus(LoaneeStatus.ADDED);

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
        try{
            secondLoaneeLoanDetail = loaneeLoanDetailsOutputPort.save(secondLoaneeLoanDetail);
            secondLoaneeLoanDetailId = secondLoaneeLoanDetail.getId();
            anotherUser = identityManagerOutputPort.createUser(anotherUser);
            anotherUser = identityOutputPort.save(anotherUser);

            anotherLoanee.setLoaneeLoanDetail(secondLoaneeLoanDetail);
            anotherLoanee.setUserIdentity(anotherUser);
            loanee = loaneeOutputPort.save(anotherLoanee);
            secondLoaneeId = loanee.getId();
            otherUserId = anotherUser.getId();
            cohortId = loanee.getCohortId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getUserIdentity().getFirstName(), anotherLoanee.getUserIdentity().getFirstName());
        assertEquals(loanee.getCohortId(), anotherLoanee.getCohortId());
    }

//    @Order(3)
//    @Test
//    void findAllLoanee(){
//
//        Loanee cohortLoanee = Loanee.builder()
//                .cohortId(firstLoanee.getCohortId())
//                .loaneeStatus(null)
//                .loanStatus(null)
//                .build();
//        List<String> foundLoaneeIds = new ArrayList<>();
//        try {
//            Page<Loanee> loanees = loaneeOutputPort.findAllLoaneeByCohortId(cohortLoanee,pageSize,pageNumber);
//            log.info("------> The loanees -----> {}", loanees.getContent());
//            assertEquals(2,loanees.toList().size());
//            foundLoaneeIds = loanees.stream()
//                    .map(Loanee::getId)
//                    .collect(Collectors.toList());
//        }catch (MeedlException exception){
//            log.error(exception.getMessage());
//        }
//        assertTrue(foundLoaneeIds.contains(loaneeId),
//                "Should contain first loanee");
//        assertTrue(foundLoaneeIds.contains(secondLoaneeId),
//                "Should contain second loanee");
//
//    }

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
        Page<Loanee> loanees = Page.empty();
        firstLoanee.setLoaneeName("le");
        try{
            loanees = loaneeOutputPort.searchForLoaneeInCohort(firstLoanee,pageSize,pageNumber);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertEquals(2,loanees.getContent().size());
    }

    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeRepository.deleteById(loaneeId);
        identityManagerOutputPort.deleteUser(userIdentity);
        identityOutputPort.deleteUserById(userId);
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetailId);

        loaneeRepository.deleteById(secondLoaneeId);
        loaneeLoanDetailsOutputPort.delete(secondLoaneeLoanDetailId);
        Optional<UserIdentity> userByEmail = identityManagerOutputPort.getUserByEmail(anotherUser.getEmail());
        if(userByEmail.isPresent()){
            identityManagerOutputPort.deleteUser(userByEmail.get());
        }
        identityOutputPort.deleteUserById(otherUserId);
    }
}
