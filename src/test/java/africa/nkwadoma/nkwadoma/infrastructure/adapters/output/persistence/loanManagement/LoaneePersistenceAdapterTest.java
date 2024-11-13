package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
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
    private int pageSize = 1;
    private int pageNumber = 2;

    private LoaneeLoanDetail loaneeLoanDetail;
    private LoanBreakdown loanBreakdown;

    @Autowired
    private LoaneeRepository loaneeRepository;
    private UserIdentity userIdentity;
    private UserIdentity anotherUser;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;



    @BeforeAll
    void setUpUserIdentity(){
        userIdentity = UserIdentity.builder().id(id).email("qudus55@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        anotherUser = UserIdentity.builder().id(secondId).email("lekan@gmail.com").firstName("lekan").lastName("ayo")
                .createdBy(secondId).role(IdentityRole.LOANEE).build();
        loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).build();
        loanBreakdown = LoanBreakdown.builder().itemName("bread").itemAmount(BigDecimal.valueOf(34))
                .currency("usd").build();
        try {
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);
            anotherUser = identityManagerOutputPort.createUser(anotherUser);
            anotherUser = identityOutputPort.save(anotherUser);
            List<LoanBreakdown> loanBreakdownList = loanBreakdownOutputPort.saveAll(List.of(loanBreakdown));
            loaneeLoanDetail.setLoanBreakdown(loanBreakdownList);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }

    }


    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setCohortId(id);
        firstLoanee.setCreatedBy(id);
        firstLoanee.setLoanee(userIdentity);
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);

        anotherLoanee = new Loanee();
        anotherLoanee.setId(secondId);
        anotherLoanee.setCreatedBy(secondId);
        anotherLoanee.setLoanee(anotherUser);
        anotherLoanee.setLoaneeLoanDetail(loaneeLoanDetail);

    }



    @Test
    void saveNullLoanee(){
        assertThrows(MeedlException.class , () -> loaneeOutputPort.save(null));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyFistName(String firstName){
        firstLoanee.getLoanee().setFirstName(firstName);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyLastName(String lastName){
        firstLoanee.getLoanee().setLastName(lastName);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));

    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyEmail(String email){
        firstLoanee.getLoanee().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveNullEmail(){
        firstLoanee.getLoanee().setEmail(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }
    @Test
    void saveNullFirstName(){
        firstLoanee.getLoanee().setFirstName(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveNullLastName(){
        firstLoanee.getLoanee().setLastName(null);
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
        firstLoanee.getLoanee().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings = {"qudussgsg", "25355366363"})
    void saveLoaneeWithInvalidEmail(String email){
        firstLoanee.getLoanee().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullEmail(){
        firstLoanee.getLoanee().setEmail(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }


    @Order(1)
    @Test
    void saveLoanee(){
        Loanee loanee = new Loanee();
        try {
             loanee = loaneeOutputPort.save(firstLoanee);
             loaneeId = loanee.getId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getLoanee().getFirstName(),firstLoanee.getLoanee().getFirstName());
        assertEquals(loanee.getCohortId(),firstLoanee.getCohortId());
    }

    @Order(2)
    @Test
    void saveAnotherLoanee(){
        Loanee loanee = new Loanee();
        try{
            loanee = loaneeOutputPort.save(anotherLoanee);
            secondLoaneeId = loanee.getId();
            cohortId = loanee.getCohortId();
        }catch (MeedlException exception){
            log.error(exception.getMessage());
        }
        assertEquals(loanee.getLoanee().getFirstName(),anotherLoanee.getLoanee().getFirstName());
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

    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeRepository.deleteById(loaneeId);
        loaneeRepository.deleteById(secondLoaneeId);
        identityOutputPort.deleteUserById(userIdentity.getId());
        identityOutputPort.deleteUserById(anotherUser.getId());
    }
}
