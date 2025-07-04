package africa.nkwadoma.nkwadoma.infrastructure.adapters.output.persistence.loanmanagement.LoanBook;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.identity.UserIdentityOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.LoaneeLoanDetailsOutputPort;
import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.identity.UserIdentity;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import africa.nkwadoma.nkwadoma.domain.model.loan.LoaneeLoanDetail;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import africa.nkwadoma.nkwadoma.testUtilities.data.TestData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    String mockId = "ead0f7cb-5483-4bb8-b271-813970a9c368";
    private Loanee loanee;
    private UserIdentity userIdentity;
    private LoaneeLoanDetail loaneeLoanDetail;
    @Autowired
    private LoaneeLoanDetailsOutputPort loaneeLoanDetailsOutputPort;
    String repaymentId = "";
    int pageSize = 10;
    int pageNumber = 0;
    private String randomId = UUID.randomUUID().toString();

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

//    @Test
//    void cannotSaveRepaymentHistoryWithNUllPaymentDate(){
//        repaymentHistory.setPaymentDateTime(null);
////        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.save(repaymentHistory));
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

    @Test
    void findRepaymentHistoryWithNullId(){
        assertThrows(MeedlException.class, () ->repaymentHistoryOutputPort.findRepaymentHistoryById(null));
    }

    @Order(7)
    @Test
    void findLatestRepaymentForLoaneeInAParticularCohort() throws MeedlException {
        RepaymentHistory firstRepaymentHistory = TestData.buildRepaymentHistory(randomId);
        firstRepaymentHistory.setPaymentDateTime(LocalDateTime.now().minusDays(3));
        firstRepaymentHistory.setAmountPaid(new BigDecimal("1000"));
        firstRepaymentHistory.setLoanee(loanee);
        firstRepaymentHistory = repaymentHistoryOutputPort.save(firstRepaymentHistory);

        RepaymentHistory secondRepaymentHistory = TestData.buildRepaymentHistory(randomId);
        secondRepaymentHistory.setPaymentDateTime(LocalDateTime.now().minusDays(2));
        secondRepaymentHistory.setAmountPaid(new BigDecimal("2000"));
        secondRepaymentHistory.setLoanee(loanee);
        secondRepaymentHistory = repaymentHistoryOutputPort.save(secondRepaymentHistory);

        RepaymentHistory thirdRepaymentHistory = TestData.buildRepaymentHistory(randomId);
        thirdRepaymentHistory.setPaymentDateTime(LocalDateTime.now().minusDays(1));
        thirdRepaymentHistory.setAmountPaid(new BigDecimal("3000.00"));
        thirdRepaymentHistory.setLoanee(loanee);
        thirdRepaymentHistory = repaymentHistoryOutputPort.save(thirdRepaymentHistory);

        RepaymentHistory latestRepaymentFound = repaymentHistoryOutputPort.findLatestRepayment(loanee.getId(), randomId);

        assertNotNull(latestRepaymentFound);
        assertEquals(thirdRepaymentHistory.getAmountPaid(), latestRepaymentFound.getAmountPaid());
        assertEquals(thirdRepaymentHistory.getPaymentDateTime(), latestRepaymentFound.getPaymentDateTime());
        assertEquals(thirdRepaymentHistory.getId(), latestRepaymentFound.getId());
        repaymentHistoryOutputPort.delete(firstRepaymentHistory.getId());
        repaymentHistoryOutputPort.delete(secondRepaymentHistory.getId());
        repaymentHistoryOutputPort.delete(thirdRepaymentHistory.getId());
    }

    @Order(8)
    @Test
    void findLatestRepaymentHistoriesWithNoneExistingIdsl() throws MeedlException {
        RepaymentHistory latest = repaymentHistoryOutputPort.findLatestRepayment(randomId, randomId);
        assertNull(latest);
    }

    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "non-existent-loanee-id" })
    public void findLatestRepaymentWithInvalidLoaneeId(String loaneeId){
        assertThrows(MeedlException.class, ()->
                repaymentHistoryOutputPort.findLatestRepayment(loaneeId, randomId));
    }
    @ParameterizedTest
    @ValueSource(strings = {StringUtils.EMPTY, StringUtils.SPACE, "non-existent-cohort-id" })
    public void findLatestRepaymentWithInvalidCohortId(String cohortId){
        assertThrows(MeedlException.class, ()->
                repaymentHistoryOutputPort.findLatestRepayment(repaymentId, cohortId));
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        repaymentHistoryOutputPort.delete(repaymentId);
        loaneeOutputPort.deleteLoanee(loanee.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
   }

}
