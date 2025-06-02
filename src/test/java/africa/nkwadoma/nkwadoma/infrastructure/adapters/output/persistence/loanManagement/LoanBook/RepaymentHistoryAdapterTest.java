package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanManagement.LoanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanManagement.loanBook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class RepaymentHistoryAdapterTest {


    @Autowired
    private RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    @Autowired
    private UserIdentityOutputPort userIdentityOutputPort;
    @Autowired
    private LoaneeOutputPort loaneeOutputPort;
    private RepaymentHistory repaymentHistory;
    String mockId = "ead0f7cb-5483-4bb8-b271-813970a9c368";
    private Loanee loanee;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    String repaymentId = "";
    int pageSize = 10;
    int pageNumber = 0;

    @BeforeAll
    void setUp() throws MeedlException {
        repaymentHistory = TestData.buildRepaymentHistory(mockId);
        userIdentity = TestData.createTestUserIdentity("idanBuruku@gmail.com");
        userIdentity = userIdentityOutputPort.save(userIdentity);
        log.info("UserIdentity: {}", userIdentity);
        loaneeLoanDetail = TestData.createTestLoaneeLoanDetail();
        loaneeLoanDetail = loaneeLoanDetailsOutputPort.save(loaneeLoanDetail);
        loanee = TestData.createTestLoanee(userIdentity,loaneeLoanDetail);
        loanee = loaneeOutputPort.save(loanee);
        log.info("Created loanee: {} " , loanee);
        repaymentHistory.setLoanee(loanee);
    }



    @Test
    void cannotSaveNullRepaymentHistory(){
        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.save(null));
    }


    @Test
    void cannotSaveRepaymentHistoryWithNUllAmountPaid(){
        repaymentHistory.setAmountPaid(null);
        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.save(repaymentHistory));
    }

    @Test
    void cannotSaveRepaymentHistoryWithNUllAmountOutstanding(){
        repaymentHistory.setAmountOutstanding(null);
        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.save(repaymentHistory));
    }

    @Test
    void cannotSaveRepaymentHistoryWithNUllPaymentDate(){
        repaymentHistory.setPaymentDateTime(null);
        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.save(repaymentHistory));
    }

    @Order(1)
    @Test
    void saveRepaymentHistory(){
        RepaymentHistory savedRepaymentHistory = null;
        try{
            log.info(" repayment history loanee {}", repaymentHistory.getLoanee());
            log.info(" repayment history loanee useridentity {}", repaymentHistory.getLoanee().getUserIdentity());
            savedRepaymentHistory = repaymentHistoryOutputPort.save(repaymentHistory);
            repaymentId = savedRepaymentHistory.getId();
        }catch (MeedlException e){
            log.info("RepaymentHistoryAdapterTest.saveRepaymentHistory(): {}", e.getMessage());
        }
        assertNotNull(savedRepaymentHistory);
        assertEquals(savedRepaymentHistory.getAmountPaid(), repaymentHistory.getAmountPaid());
    }

    @Order(2)
    @Test
    void findLoaneeRepaymentHistory(){
        Page<RepaymentHistory> repaymentHistories = Page.empty();
        try {
            repaymentHistories = repaymentHistoryOutputPort.findAllRepaymentHistoryAttachedToLoanee(loanee.getId(),pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.info("RepaymentHistoryAdapterTest.findLoaneeRepaymentHistory(): {}", meedlException.getMessage());
        }
        assertNotNull(repaymentHistories);
        assertTrue(repaymentHistories.hasContent());
    }


   @AfterAll
    void cleanUp() throws MeedlException {
        repaymentHistoryOutputPort.delete(repaymentId);
        loaneeOutputPort.deleteLoanee(loanee.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
   }

}
