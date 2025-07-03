package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement.LoanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.loanee.ModeOfPayment;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.LoaneeEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.entity.loanentity.RepaymentHistoryEntity;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.mapper.LoaneeMapper;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private String mockId = "ead0f7cb-5483-4bb8-b271-813970a9c368";
    private Loanee loanee;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;
    private List<String> ids = new ArrayList<>();
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    private String repaymentId = "";
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
    private void saveTestRepaymentHistories(List<String>ids) throws MeedlException {
        LocalDateTime baseDate = LocalDateTime.of(2025, 7, 1, 10, 0); // July 1, 2025, 10:00 AM

        repaymentHistory.setPaymentDateTime(baseDate.withHour(10));
        repaymentHistory.setAmountPaid(new BigDecimal("100"));
        repaymentHistory.setId(null);
        RepaymentHistory savedHistory = repaymentHistoryOutputPort.save(repaymentHistory);
        ids.add(savedHistory.getId());

        repaymentHistory.setPaymentDateTime(baseDate.withHour(12));
        repaymentHistory.setAmountPaid(new BigDecimal("200"));
        repaymentHistory.setId(null);
        savedHistory = repaymentHistoryOutputPort.save(repaymentHistory);
        ids.add(savedHistory.getId());

        repaymentHistory.setPaymentDateTime(LocalDateTime.now());
        repaymentHistory.setAmountPaid(new BigDecimal("300"));
        repaymentHistory.setId(null);
        savedHistory = repaymentHistoryOutputPort.save(repaymentHistory);
        ids.add(savedHistory.getId());

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

//    @Test
//    void cannotSaveRepaymentHistoryWithNUllPaymentDate(){
//        repaymentHistory.setPaymentDateTime(null);
////       assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.save(repaymentHistory));
//    }

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
    void findAllLoaneeRepaymentHistory(){
        Page<RepaymentHistory> repaymentHistories = Page.empty();
        repaymentHistory.setLoaneeId(loanee.getId());
        try {
            repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.info("RepaymentHistoryAdapterTest.findAllLoaneeRepaymentHistory(): {}", meedlException.getMessage());
        }
        assertNotNull(repaymentHistories);
        assertTrue(repaymentHistories.hasContent());
    }

    @Order(3)
    @Test
    void findAllLoaneeRepaymentHistoryByMonth(){
        Page<RepaymentHistory> repaymentHistories = Page.empty();
        repaymentHistory.setMonth(LocalDateTime.now().getMonthValue());
        try {
            repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.info("RepaymentHistoryAdapterTest.findAllLoaneeRepaymentHistory(): {}", meedlException.getMessage());
        }
        assertNotNull(repaymentHistories);
        assertTrue(repaymentHistories.hasContent());
    }

    @Order(4)
    @Test
    void findAllLoaneeRepaymentHistoryByYear(){
        Page<RepaymentHistory> repaymentHistories = Page.empty();
        repaymentHistory.setYear(LocalDateTime.now().getYear());
        try {
            repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.info("RepaymentHistoryAdapterTest.findAllLoaneeRepaymentHistory(): {}", meedlException.getMessage());
        }
        assertNotNull(repaymentHistories);
        assertTrue(repaymentHistories.hasContent());
    }

    @Order(5)
    @Test
    void findLoaneeRepaymentHistoryByRepaymentId(){
        RepaymentHistory foundRepaymentHistory = null;
        try{
            foundRepaymentHistory = repaymentHistoryOutputPort.findRepaymentHistoryById(repaymentId);
        }catch (MeedlException meedlException){
            log.info("RepaymentHistoryAdapterTest.findLoaneeRepaymentHistory(): {}", meedlException.getMessage());
        }
        assertNotNull(foundRepaymentHistory);
        assertEquals(foundRepaymentHistory.getId(), repaymentId);
    }

    @Order(6)
    @Test
    void searchRepaymentHistoryByLoaneeName(){
        Page<RepaymentHistory> repaymentHistories = Page.empty();
        repaymentHistory.setLoaneeName("d");
        try {
            repaymentHistories = repaymentHistoryOutputPort.findRepaymentHistoryAttachedToALoaneeOrAll(repaymentHistory,pageSize,pageNumber);
        }catch (MeedlException meedlException){
            log.info("RepaymentHistoryAdapterTest.findAllLoaneeRepaymentHistory(): {}", meedlException.getMessage());
        }
        assertNotNull(repaymentHistories);
        assertTrue(repaymentHistories.hasContent());
    }
    @Order(7)
    @Test
    void testFindLatestRepaymentMultipleSameDateDifferentTimes() {

        RepaymentHistory latest = null;
        try {
            saveTestRepaymentHistories(ids);
            latest = repaymentHistoryOutputPort.findLatestRepayment(loanee.getId(), repaymentHistory.getCohort().getId());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

        assertNotNull(latest);
        assertEquals(new BigDecimal("300.00"), latest.getAmountPaid());
    }


    @Test
    void trowExceptionIfFindingRepaymentHistoryWithNullId(){
        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.findRepaymentHistoryById(null));
    }


   @AfterAll
    void cleanUp() throws MeedlException {
        repaymentHistoryOutputPort.delete(repaymentId);
        repaymentHistoryOutputPort.delete(ids.get(0));
        repaymentHistoryOutputPort.delete(ids.get(1));
        repaymentHistoryOutputPort.delete(ids.get(2));
        loaneeOutputPort.deleteLoanee(loanee.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
   }

}
