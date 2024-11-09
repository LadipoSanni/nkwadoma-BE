package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
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

import java.math.BigDecimal;

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
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private String loaneeId;

    private LoaneeLoanDetail loaneeLoanDetail;

    @Autowired
    private LoaneeRepository loaneeRepository;
    private UserIdentity userIdentity;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;



    @BeforeAll
    void setUpUserIdentity(){
        userIdentity = UserIdentity.builder().email("qudus55@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        try {
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }

    }


    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setOrganizationId(id);
        firstLoanee.setProgramId(id);
        firstLoanee.setCohortId(id);
        firstLoanee.setCreatedBy(id);
        firstLoanee.setUser(userIdentity);
        loaneeLoanDetail = new LoaneeLoanDetail();
        loaneeLoanDetail.setAmountRequested(BigDecimal.valueOf(4000));
        loaneeLoanDetail.setInitialDeposit(BigDecimal.valueOf(200));
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);
    }



    @Test
    void saveNullLoanee(){
        assertThrows(MeedlException.class , () -> loaneeOutputPort.save(null));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyFistName(String firstName){
        firstLoanee.getUser().setFirstName(firstName);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyLastName(String lastName){
        firstLoanee.getUser().setLastName(lastName);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));

    }

    @ParameterizedTest
    @ValueSource(strings= {StringUtils.EMPTY, StringUtils.SPACE})
    void saveEmptyEmail(String email){
        firstLoanee.getUser().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveNullEmail(){
        firstLoanee.getUser().setEmail(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }
    @Test
    void saveNullFirstName(){
        firstLoanee.getUser().setFirstName(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveNullLastName(){
        firstLoanee.getUser().setLastName(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void saveLoaneeWithEmptyOrganizationId(String organizationId){
        firstLoanee.setOrganizationId(organizationId);
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

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY,StringUtils.SPACE})
    void saveLoaneeWithEmptyProgramId(String programId){
        firstLoanee.setProgramId(programId);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }


    @Test
    void saveLoaneeWithNullCohortId(){
        firstLoanee.setCohortId(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullProgramId(){
        firstLoanee.setProgramId(null);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullOrganizationId(){
        firstLoanee.setOrganizationId(null);
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
        firstLoanee.getUser().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @ParameterizedTest
    @ValueSource(strings = {"qudussgsg", "25355366363"})
    void saveLoaneeWithInvalidEmail(String email){
        firstLoanee.getUser().setEmail(email);
        assertThrows(MeedlException.class,()-> loaneeOutputPort.save(firstLoanee));
    }

    @Test
    void saveLoaneeWithNullEmail(){
        firstLoanee.getUser().setEmail(null);
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
        assertEquals(loanee.getUser().getFirstName(),firstLoanee.getUser().getFirstName());
        assertEquals(loanee.getCohortId(),firstLoanee.getCohortId());
    }



    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeRepository.deleteById(loaneeId);
        identityOutputPort.deleteUserById(userIdentity.getId());
        identityManagerOutputPort.deleteUser(userIdentity);
    }
}
