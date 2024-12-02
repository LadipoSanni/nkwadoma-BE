package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement;
import africa.nkwadoma.nkwadoma.application.ports.output.loan.LoaneeLoanAccountOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.AccountStatus;
import africa.nkwadoma.nkwadoma.domain.enums.loanEnums.LoanStatus;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanAccount;
import africa.nkwadoma.nkwadoma.test.data.TestData;
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

    @BeforeAll
    void setUpLoaneeLoanAccount(){
        loaneeLoanAccount = TestData.createLoaneeLoanAccount(LoanStatus.AWAITING_DISBURSAL, AccountStatus.NEW);
    }

    @Test
    void saveLoaneeLoanAccountWithNullLoanStatus(){
        loaneeLoanAccount.setLoanStatus(null);
        assertThrows(MeedlException.class,()-> loaneeLoanAccountOutputPort.save(loaneeLoanAccount));
    }

    @Test
    void saveLoaneeLoanAccountWithNullAccountStatus(){
        loaneeLoanAccount.setStatus(null);
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
        assertEquals(AccountStatus.NEW, loaneeLoanAccount.getStatus());
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        loaneeLoanAccountOutputPort.deleteLoaneeLoanAccount(loaneeLoanAccount.getId());
    }
}
