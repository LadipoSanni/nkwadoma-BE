package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanAccountOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.AccountStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanenums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class LoaneeLoanAccountAdapterTest {


    private LoaneeLoanAccount loaneeLoanAccount;
    @Autowired
    private LoaneeLoanAccountOutputPort loaneeLoanAccountOutputPort;
    private String loaneeLoanAccountId;
    private String loaneeId = "96f2eb2b-1a78-4838-b5d8-66e95cc9ae9f";

    @BeforeAll
    void setUpLoaneeLoanAccount(){
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW,loaneeId);
    }

    @Test
    void saveLoaneeLoanAccountWithNullLoanStatus(){
        loaneeLoanAccount.setLoanStatus(null);
        assertThrows(MeedlException.class,()-> loaneeLoanAccountOutputPort.save(loaneeLoanAccount));
    }

    @Test
    void saveLoaneeLoanAccountWithNullAccountStatus(){
        loaneeLoanAccount.setAccountStatus(null);
        assertThrows(MeedlException.class,()-> loaneeLoanAccountOutputPort.save(loaneeLoanAccount));
    }

    @Test
    @Order(1)
    void saveLoaneeLoanAccount(){
        try{
            loaneeLoanAccount = loaneeLoanAccountOutputPort.save(loaneeLoanAccount);
        }catch (MeedlException e){
            log.error(e.getMessage());
        }
        log.info("loan account id {} ",loaneeLoanAccountId);
        assertEquals(LoanStatus.AWAITING_DISBURSAL, loaneeLoanAccount.getLoanStatus() );
        assertEquals(AccountStatus.NEW, loaneeLoanAccount.getAccountStatus());
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(loaneeLoanAccount.getId());
    }
}
