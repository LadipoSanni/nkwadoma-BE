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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static africa.nkwadoma.nkwadoma.domain.enums.constants.loan.FinancialConstants.NUMBER_OF_DECIMAL_PLACES;
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
    private LocalDateTime roundToMicroseconds(LocalDateTime dateTime) {
        int nano = dateTime.getNano();
        int micro = (nano + 500) / 1000; // round to nearest microsecond
        return dateTime.withNano(micro * 1000);
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
        assertEquals(decimalPlaceRoundUp(new BigDecimal("300.00")), decimalPlaceRoundUp(latest.getAmountPaid()));
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
        assertEquals(decimalPlaceRoundUp(thirdRepaymentHistory.getAmountPaid()), decimalPlaceRoundUp(latestRepaymentFound.getAmountPaid()));
        assertEquals(roundToMicroseconds(thirdRepaymentHistory.getPaymentDateTime()), roundToMicroseconds(latestRepaymentFound.getPaymentDateTime()));
        assertEquals(thirdRepaymentHistory.getId(), latestRepaymentFound.getId());
        repaymentHistoryOutputPort.delete(firstRepaymentHistory.getId());
        repaymentHistoryOutputPort.delete(secondRepaymentHistory.getId());
        repaymentHistoryOutputPort.delete(thirdRepaymentHistory.getId());
    }
    private BigDecimal decimalPlaceRoundUp(BigDecimal bigDecimal) {
        return bigDecimal.setScale(NUMBER_OF_DECIMAL_PLACES, RoundingMode.HALF_UP);
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

    @Order(9)
    @Test
    void findAllRepaymentHistoryForValidLoaneeAndCohort() throws MeedlException {
        String cohortId = UUID.randomUUID().toString();

        RepaymentHistory firstRepayment = TestData
                .buildRepaymentHistory(cohortId, "500.00", LocalDateTime.now().minusDays(3) );
        firstRepayment.getLoanee().setId(loanee.getId());
        firstRepayment.getLoanee().getUserIdentity().setId(userIdentity.getId());
        firstRepayment = repaymentHistoryOutputPort.save(firstRepayment);
        ids.add(firstRepayment.getId());

        RepaymentHistory secondRepayment = TestData
                .buildRepaymentHistory(cohortId, "800.00", LocalDateTime.now().minusDays(2));
        secondRepayment.getLoanee().setId(loanee.getId());
        secondRepayment.getLoanee().getUserIdentity().setId(userIdentity.getId());
        secondRepayment = repaymentHistoryOutputPort.save(secondRepayment);
        ids.add(secondRepayment.getId());

        List<RepaymentHistory> found = repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loanee.getId(), cohortId);

        assertNotNull(found);
        assertEquals(2, found.size());
        assertTrue(found.get(0).getPaymentDateTime().isBefore(found.get(1).getPaymentDateTime()));
    }
    @Order(10)
    @Test
    void findAllRepaymentHistoryForValidLoaneeButInvalidCohort() throws MeedlException {
        List<RepaymentHistory> result = repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loanee.getId(), UUID.randomUUID().toString());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Order(11)
    @Test
    void findAllRepaymentHistoryWithInvalidUUID() {
        String invalidUuid = "not-a-uuid";

        assertThrows(MeedlException.class, () ->
                repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(invalidUuid, invalidUuid));

        assertThrows(MeedlException.class, () ->
                repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loanee.getId(), invalidUuid));
    }
    @Order(12)
    @Test
    void findAllRepaymentHistoryForLoaneeWithNoPayments() throws MeedlException {
        UserIdentity userIdentity = TestData.createTestUserIdentity("userwithnorepyament@email.com", UUID.randomUUID().toString());
        userIdentityOutputPort.save(userIdentity);
        Loanee newLoanee = loaneeOutputPort.save(
                TestData.createTestLoanee(userIdentity, loaneeLoanDetail)
        );

        List<RepaymentHistory> result = repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(newLoanee.getId(), UUID.randomUUID().toString());

        assertNotNull(result);
        assertTrue(result.isEmpty());

        loaneeOutputPort.deleteLoanee(newLoanee.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
    }


    @AfterAll
    void cleanUp() throws MeedlException {
        repaymentHistoryOutputPort.delete(repaymentId);
        for (String id : ids) {
            repaymentHistoryOutputPort.delete(id);
        }
        loaneeOutputPort.deleteLoanee(loanee.getId());
        userIdentityOutputPort.deleteUserById(userIdentity.getId());
        loaneeLoanDetailsOutputPort.delete(loaneeLoanDetail.getId());
   }

}
