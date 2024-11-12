package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.IdentityManagerOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoanBreakdownOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.education.LoanBreakdown;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class CohortLoaneePersistenceAdapterTest {


    @Autowired
    private CohortLoaneeOutputPort cohortLoaneeOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    @Autowired
    private CohortLoaneeRepository cohortLoaneeRepository;
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private Loanee firstLoanee ;
    private CohortLoanee cohortLoanee ;
    private LoaneeLoanDetail loaneeLoanDetail;
    private String  cohortLoaneeId;
    private String  loaneeId;
    private UserIdentity userIdentity;
    private LoanBreakdown loanBreakdown;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private IdentityManagerOutputPort identityManagerOutputPort;
    @Autowired
    private LoanBreakdownOutputPort loanBreakdownOutputPort;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;



    @BeforeAll
    public void saveSetUp(){
        userIdentity = UserIdentity.builder().email("qudus5445@gmail.com").firstName("qudus").lastName("lekan")
                .createdBy(id).role(IdentityRole.LOANEE).build();
        loaneeLoanDetail = LoaneeLoanDetail.builder().amountRequested(BigDecimal.valueOf(4000))
                .initialDeposit(BigDecimal.valueOf(200)).build();
        loanBreakdown = LoanBreakdown.builder().itemName("bread").itemAmount(BigDecimal.valueOf(34))
                .currency("usd").build();

        try{
            userIdentity = identityManagerOutputPort.createUser(userIdentity);
            userIdentity = identityOutputPort.save(userIdentity);
            List<LoanBreakdown> loanBreakdownList = loanBreakdownOutputPort.saveAll(List.of(loanBreakdown));
            loaneeLoanDetail.setLoanBreakdown(loanBreakdownList);
            loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        }catch (MeedlException e) {
            log.error(e.getMessage());
        }


    }

    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setCohortId(id);
        firstLoanee.setCreatedBy(id);
        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);
        firstLoanee.setLoanee(userIdentity);
        cohortLoanee = new CohortLoanee();
        cohortLoanee.setCohort(firstLoanee.getCohortId());
        cohortLoanee.setLoanee(firstLoanee);

    }


    @Test
    void saveCohortLoanee(){
        CohortLoanee savedCohortLoanee = new CohortLoanee();
        try{
            Loanee loanee = loaneeOutputPort.save(firstLoanee);
            loaneeId = loanee.getId();
            savedCohortLoanee = cohortLoaneeOutputPort.save(loanee);
            cohortLoaneeId = savedCohortLoanee.getId();
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        assertEquals(cohortLoanee.getCohort(), savedCohortLoanee.getCohort());
        assertEquals(cohortLoanee.getLoanee().getLoanee().getFirstName(),
                savedCohortLoanee.getLoanee().getLoanee().getFirstName());
    }


    @AfterAll
    void tearDown() throws MeedlException {
        cohortLoaneeRepository.deleteById(cohortLoaneeId);
        loaneeOutputPort.deleteLoanee(loaneeId);
        identityOutputPort.deleteUserById(userIdentity.getId());
        identityManagerOutputPort.deleteUser(userIdentity);
    }
}
