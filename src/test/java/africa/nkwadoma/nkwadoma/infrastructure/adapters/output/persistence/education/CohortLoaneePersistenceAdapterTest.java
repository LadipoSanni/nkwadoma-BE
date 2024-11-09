package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.education;


import africa.nkwadoma.nkwadoma.application.ports.output.education.CohortLoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.education.CohortLoanee;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.repository.education.CohortLoaneeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

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


    @BeforeEach
    public void setUp(){
        firstLoanee = new Loanee();
        firstLoanee.setOrganizationId(id);
        firstLoanee.setProgramId(id);
        firstLoanee.setCohortId(id);
        firstLoanee.setCreatedBy(id);

        userIdentity = new UserIdentity();
        userIdentity.setEmail("qudusa55@gmail.com");
        userIdentity.setFirstName("ned");
        userIdentity.setLastName("tade");

        loaneeLoanDetail = new LoaneeLoanDetail();
        loaneeLoanDetail.setAmountRequested(BigDecimal.ZERO);
        loaneeLoanDetail.setInitialDeposit(BigDecimal.ZERO);

        firstLoanee.setLoaneeLoanDetail(loaneeLoanDetail);

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
        assertEquals(cohortLoanee.getLoanee().getUser().getFirstName(),
                savedCohortLoanee.getLoanee().getUser().getFirstName());
    }


    @AfterAll
    void tearDown(){
        cohortLoaneeRepository.deleteById(cohortLoaneeId);
        loaneeOutputPort.deleteLoanee(loaneeId);
    }
}
