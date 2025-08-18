package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.loanmanagement;


import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAggregateOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.identity.IdentityRole;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAggregate;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoaneeLoanAggregateAdapterTest{


    private UserIdentity userIdentity;
    private String id = "5bc2ef97-1035-4e42-bc8b-22a90b809f7c";
    private Loanee loanee;
    @Autowired
    private UserIdentityOutputPort identityOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    private LoaneeLoanAggregate loaneeLoanAggregate;
    @Autowired
    private LoaneeLoanAggregateOutputPort loaneeLoanAggregateOutputPort;
    private String loaneeLoanAggregateId;



    @BeforeAll
    void setUp(){
        try {
            userIdentity = UserIdentity.builder().id(id).email("lekan@gmail.com").firstName("qudus").lastName("lekan")
                    .createdBy(id).role(IdentityRole.LOANEE).build();
            userIdentity = identityOutputPort.save(userIdentity);
            loanee = TestData.createTestLoanee(userIdentity, null);
            loanee = loaneeOutputPort.save(loanee);
            loaneeLoanAggregate = TestData.buildLoaneeLoanAggregate(loanee);
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
    }


    @Test
    void saveLoaneeLoanAggregateWithNullLoanee(){
        loaneeLoanAggregate.setLoanee(null);
        assertThrows(MeedlException.class,()-> loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate));
    }

    @Test
    void saveLoaneeLoanAggregateWithNullHistoricalAmount(){
        loaneeLoanAggregate.setHistoricalDebt(null);
        assertThrows(MeedlException.class,()-> loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate));
    }

    @Test
    void saveLoaneeLoanAggregateWithNullAmountOustanding(){
        loaneeLoanAggregate.setTotalAmountOutstanding(null);
        assertThrows(MeedlException.class,()-> loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate));
    }

    @Order(1)
    @Test
    void saveLoaneeLoanAggregate(){
        LoaneeLoanAggregate saveLoaneeLoanAggregate = null;
        try {
            saveLoaneeLoanAggregate = loaneeLoanAggregateOutputPort.save(loaneeLoanAggregate);
            loaneeLoanAggregateId = saveLoaneeLoanAggregate.getId();
        }catch (MeedlException meedlException){
            log.error(meedlException.getMessage());
        }
        assertNotNull(saveLoaneeLoanAggregate);
        assertEquals(saveLoaneeLoanAggregate.getNumberOfLoans(),loaneeLoanAggregate.getNumberOfLoans());
    }

    @Order(2)
    @Test
    void findLoaneeLoanAggregateByLoaneeId(){
        LoaneeLoanAggregate foundLoaneeLoanAggregate = null;
        try {
            foundLoaneeLoanAggregate = loaneeLoanAggregateOutputPort.findByLoaneeId(loanee.getId());
        } catch (MeedlException e) {
            log.error(e.getMessage());
        }
        assertNotNull(foundLoaneeLoanAggregate);
        assertEquals(foundLoaneeLoanAggregate.getNumberOfLoans(),loaneeLoanAggregate.getNumberOfLoans());
    }

    @Order(3)
    @Test
    void findAllLoaneeLoanAggregate(){
        Page<LoaneeLoanAggregate> loaneeLoanAggregates = null;
        loaneeLoanAggregates = loaneeLoanAggregateOutputPort.findAllLoanAggregate(10,0);
        assertNotNull(loaneeLoanAggregates);
        assertEquals(1, loaneeLoanAggregates.getNumberOfElements());
    }

    @Order(4)
    @Test
    void searchLoaneeLoanAggregate(){
        Page<LoaneeLoanAggregate> loaneeLoanAggregates = null;
        loaneeLoanAggregates = loaneeLoanAggregateOutputPort.searchLoanAggregate("q",10,0);
        assertNotNull(loaneeLoanAggregates);
        assertEquals(1, loaneeLoanAggregates.getNumberOfElements());
    }

    @AfterAll
    void tearDown() throws MeedlException {
        loaneeLoanAggregateOutputPort.delete(loaneeLoanAggregateId);
        loaneeOutputPort.deleteLoanee(loanee.getId());
        identityOutputPort.deleteUserById(userIdentity.getId());
    }

}
