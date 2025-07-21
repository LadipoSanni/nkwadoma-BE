package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.output.loanmanagement.loanbook.RepaymentHistoryOutputPort;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanCalculationMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.RepaymentHistory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class LoanCalculationServiceTest {
    @InjectMocks
    private CalculationEngine calculationEngine;
    @Mock
    private RepaymentHistoryOutputPort repaymentHistoryOutputPort;
    private String loaneeId;
    private String cohortId;
    private final BigDecimal ZERO = new BigDecimal("0.00");
    private final int NUMBER_OF_DECIMAL_PLACE = 8;

    @BeforeEach
    void setup() {
        loaneeId = UUID.randomUUID().toString();
        cohortId = UUID.randomUUID().toString();
    }

    private RepaymentHistory createRepayment(LocalDateTime time, BigDecimal amount) {
        return RepaymentHistory.builder()
                .paymentDateTime(time)
                .amountPaid(amount)
                .build();
    }


    @BeforeEach
    void setUp() {
    }
    private BigDecimal decimalPlaceRoundUp(BigDecimal bigDecimal) {
        return bigDecimal.setScale(NUMBER_OF_DECIMAL_PLACE, RoundingMode.HALF_UP);
    }

    @Test
    public void calculateLoanAmountRequested() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountRequested(
                    new BigDecimal("10000.00"),
                    new BigDecimal("2500.00")
            );
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("7500.00"), result);
    }

    @Test
    public void returnsZeroWhenInitialDepositEqualsProgramFee() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountRequested(
                    new BigDecimal("5000.00"),
                    new BigDecimal("5000.00")
            );
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(ZERO, result);
    }

    @Test
    public void returnsNegativeWhenDepositExceedsProgramFee() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountRequested(
                    new BigDecimal("4000.00"),
                    new BigDecimal("4500.00")
            );
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("-500.00"), result);
    }

    @Test
    public void calculateLoanAmountRequestedWithNullProgramFee() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountRequested(null, new BigDecimal("1000.00"))
        );
        assertEquals("Program Fee must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedWithNullInitialDeposit() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountRequested(new BigDecimal("1000.00"), null)
        );
        assertEquals("Initial Deposit must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedWithNegativeProgramFee() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountRequested(new BigDecimal("-1000.00"), new BigDecimal("200.00"))
        );
        assertEquals("Program Fee must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedWithNegativeInitialDeposit() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountRequested(new BigDecimal("2000.00"), new BigDecimal("-300.00"))
        );
        assertEquals("Initial Deposit must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedAllowsZeroProgramFeeAndInitialDeposit() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountRequested(ZERO, ZERO);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(ZERO, result);
    }



    @Test
    public void calculateLoanAmountDisbursed() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountDisbursed(
                    new BigDecimal("7000.00"),
                    new BigDecimal("500.00")
            );
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("7500.00"), result);
    }

    @Test
    public void calculateLoanAmountDisbursedWithNullAmountRequested() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountDisbursed(null, new BigDecimal("100.00"))
        );
        assertEquals("Loan Amount Requested must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountDisbursedWithNullDisbursementFees() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountDisbursed(new BigDecimal("8000.00"), null)
        );
        assertEquals("Loan Disbursement Fees must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountDisbursedWithNegativeAmountRequested() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountDisbursed(new BigDecimal("-3000.00"), new BigDecimal("100.00"))
        );
        assertEquals("Loan Amount Requested must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountDisbursedWithNegativeDisbursementFees() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountDisbursed(new BigDecimal("4000.00"), new BigDecimal("-100.00"))
        );
        assertEquals("Loan Disbursement Fees must not be negative.", exception.getMessage());
    }



    @Test
    public void calculatesMonthlyInterest() {
        int result = 0;
        try {
            result = calculationEngine.calculateMonthlyInterestRate(60);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(5, result);
    }

    @Test
    public void handlesInterestRateOfZero() {
        int result = 0;
        try {
            result = calculationEngine.calculateMonthlyInterestRate(0);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(0, result);
    }

    @Test
    public void calculatesInterestRateOfOne() {
        int result = 0;
        try {
            result = calculationEngine.calculateMonthlyInterestRate(1);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(0, result);
    }

    @Test
    public void calculatesInterestRateOfTwelve() {
        int result = 0;
        try {
            result = calculationEngine.calculateMonthlyInterestRate(12);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(1, result);
    }

    @Test
    public void calculatesInterestRateOfOneHundred() {
        int result = 0;
        try {
            result = calculationEngine.calculateMonthlyInterestRate(100);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(8, result);
    }

    @Test
    public void calculateInterestRateIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMonthlyInterestRate(-1)
        );
        assertEquals("Interest rate must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateInterestRateGreaterThanHundred() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMonthlyInterestRate(101)
        );
        assertEquals("Interest rate must not exceed 100.", exception.getMessage());
    }


    @Test
    public void calculateOutstandingAmount() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountOutstanding(
                    new BigDecimal("10000.00"),
                    new BigDecimal("1500.00"),
                    100
            );
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("8600.00"), result);
    }

    @Test
    public void handlesZeroValuesGracefully() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateLoanAmountOutstanding(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0
            );
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void calculateLoanAmountOutstandingWithNegativeAmountOutstanding() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountOutstanding(new BigDecimal("-1"), new BigDecimal("100"), 10)
        );
        assertEquals("Loan Amount Outstanding must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateLoanAmountOutstandingWithNegativeMonthlyRepayment() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountOutstanding(new BigDecimal("100"), new BigDecimal("-1"), 10)
        );
        assertEquals("Monthly Repayment must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateLoanAmountOutstandingWithNullAmountOutstanding() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountOutstanding(null, new BigDecimal("500"), 20)
        );
        assertEquals("Loan Amount Outstanding must not be null.", ex.getMessage());
    }

    @Test
    public void calculateLoanAmountOutstandingWithNullMonthlyRepayment() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountOutstanding(new BigDecimal("500"), null, 20)
        );
        assertEquals("Monthly Repayment must not be null.", ex.getMessage());
    }

    @Test
    public void calculateAmountOutstandingWithNegativeMoneyWeightedPeriodicInterest() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountOutstanding(new BigDecimal("1000"), new BigDecimal("500"), -5)
        );
        assertEquals("Money Weighted Periodic Interest must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateAmountOutstandingWithInterestAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanAmountOutstanding(new BigDecimal("1000"), new BigDecimal("500"), 101)
        );
        assertEquals("Money Weighted Periodic Interest must not exceed 100.", ex.getMessage());
    }



    @Test
    public void calculateWeightedInterest() {
        List<LoanPeriodRecord> records = Arrays.asList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10),
                new LoanPeriodRecord(new BigDecimal("2000.00"), 5)
        );

        BigDecimal result = null;
        try {
            result = calculationEngine.calculateMoneyWeightedPeriodicInterest(10, records);
        } catch (MeedlException e) {
            log.error("",e);
        }

        //// For easy understanding.
        //// Interest = 10
        //// Interest / 365 = 10/363 = 0.0274
        //// Summation of Loan period record = ((1000*10) + (2000*5)) = (10000 + 10000) = 20000
        //// 0.0274 * 20000 = 547.94520
        //// Expected: (10 / 365) * ((1000*10) + (2000*5)) = (10 / 365) * (10000 + 10000) = (10 / 365) * 20000
        BigDecimal expected = new BigDecimal("10")
                .divide(new BigDecimal("365"), 8, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("20000"));
        log.info("Expected : {}, actual : {}", expected, result);
        assertNotNull(result);
        assertEquals(0, result.compareTo(expected));
    }

    @Test
    public void calculatesWithZeroInterestRate() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1500.00"), 30)
        );

        BigDecimal result = null;
        try {
            result = calculationEngine.calculateMoneyWeightedPeriodicInterest(0, records);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertNotNull(result);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));

    }

    @Test
    public void calculatePeriodicInterestWhenNoLoanPeriodsProvidedReturnsZero() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateMoneyWeightedPeriodicInterest(5, Collections.emptyList());
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertNotNull(result);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));

    }

    @Test
    public void calculateWithNegativeInterestRateInLoanPeriod() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10));

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMoneyWeightedPeriodicInterest(-1, records)
        );
        assertEquals("Interest rate must not be negative.", ex.getMessage());
    }

    @Test
    public void calculatePeriodicInterestWithInterestRateAbove100() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMoneyWeightedPeriodicInterest(101, records)
        );
        assertEquals("Interest rate must not exceed 100.", ex.getMessage());
    }

    @Test
    public void calculatePeriodicInterestWithNegativeInterestRate() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("-100.00"), 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMoneyWeightedPeriodicInterest(100, records)
        );
    }

    @Test
    public void calculateMoneyWeightedPeriodicInterestWithNegativeDaysHeld() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("100.00"), -10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMoneyWeightedPeriodicInterest(100, records));
    }

    @Test
    public void calculateMoneyWeightedPeriodicInterestWithNullAmountOutstanding() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(null, 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateMoneyWeightedPeriodicInterest(100, records));
    }



    @Test
    public void calculateManagementFee() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), 5.5);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("550.00000000"), result);
    }

    @Test
    public void calculationReturnsZeroWhenPercentageIsZero() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), 0);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertNotNull(result);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    public void calculateManagementFeeWithHundredPercent() {
        BigDecimal result = null;
        try {
            result = calculationEngine.calculateManagementOrProcessingFee(new BigDecimal("2000.00"), 100);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("2000.00000000"), result);
    }

    @Test
    public void calculateManagementFeeWithNulLoanAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateManagementOrProcessingFee(null, 10)
        );
        assertEquals("Loan Amount Requested must not be null.", ex.getMessage());
    }

    @Test
    public void calculateManagementFeeWithNegativeLoanAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateManagementOrProcessingFee(new BigDecimal("-5000.00"), 10)
        );
        assertEquals("Loan Amount Requested must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateManagementFeeWithNegativeMgtFeeInPercent() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), -1)
        );
        assertEquals("Management Fee Percentage must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateManagementFeeWithMgtFeeInPercentAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), 101)
        );
        assertEquals("Management Fee Percentage must not exceed 100.", ex.getMessage());
    }




    @Test
    public void calculateCreditLifeCorrectlyForOneYearTenure() throws MeedlException {
        BigDecimal result = calculationEngine.calculateCreditLife(new BigDecimal("10000.00"), 2, 12);
        assertEquals(new BigDecimal("200.00000000"), result);
    }

    @Test
    public void calculateCreditLifeForMoreThanOneYearTenure() throws MeedlException {
        BigDecimal result = calculationEngine.calculateCreditLife(new BigDecimal("10000.00"), 2, 14);
        assertEquals(new BigDecimal("400.00000000"), result);
    }

    @Test
    public void calculateCreditLifeDefaultsToOneYearIfLessThanTwelveMonths() throws MeedlException {
        BigDecimal result = calculationEngine.calculateCreditLife(new BigDecimal("5000.00"), 3, 5);
        assertEquals(new BigDecimal("150.00000000"), result);
    }

    @Test
    public void calculateCreditLifeReturnsZeroForZeroPercentage() throws MeedlException {
        BigDecimal result = calculationEngine.calculateCreditLife(new BigDecimal("8000.00"), 0, 24);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    public void calculateCreditLifeWithNegativeAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateCreditLife(new BigDecimal("-5000.00"), 2, 12)
        );
        assertEquals("Loan Amount Requested must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeWithNullAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateCreditLife(null, 2, 12)
        );
        assertEquals("Loan Amount Requested must not be null.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeWithNegativePercentage() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateCreditLife(new BigDecimal("10000.00"), -5, 12)
        );
        assertEquals("Credit Life Percentage must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeForPercentageAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateCreditLife(new BigDecimal("10000.00"), 101, 12)
        );
        assertEquals("Credit Life Percentage must not exceed 100.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeWithNegativeLoanTenure() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateCreditLife(new BigDecimal("10000.00"), 5, -6)
        );
        assertEquals("Loan Tenure must not be negative.", ex.getMessage());
    }



    @Test
    public void calculateDisbursementFees() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of(
                "Credit Life", new BigDecimal("100.00"),
                "Management Fee", new BigDecimal("200.00")
        );

        BigDecimal result = calculationEngine.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("300.00")), result);
    }

    @Test
    public void calculateDisbursementFeesCorrectlyWhenOneEntryIsProvided() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of("Credit Life", new BigDecimal("75.00"));

        BigDecimal result = calculationEngine.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("75.00")), result);
    }

    @Test
    public void calculateDisbursementFeesCorrectlyWhenFourEntriesAreProvided() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of(
                "Credit Life", new BigDecimal("50.00"),
                "Management Fee", new BigDecimal("25.00"),
                "Processing Fee", new BigDecimal("30.00"),
                "Other Fee", new BigDecimal("20.00")
        );

        BigDecimal result = calculationEngine.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("125.00")), result);
    }

    @Test
    public void returnsZeroWhenNoFeesProvided() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of();
        BigDecimal result = calculationEngine.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(BigDecimal.ZERO), result);
    }

    @Test
    public void calculateDisbursementFeesWithNegativeProcessingFee() {
        Map<String, BigDecimal> fees = Map.of("Processing Fee", new BigDecimal("-50.00"));

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculationEngine.calculateLoanDisbursementFees(fees)
        );
        assertEquals("Processing Fee must not be negative.", ex.getMessage());
    }





    @Test
    public void calculateTotalRepaymentForThreePayments() throws MeedlException {
        List<BigDecimal> repayments = List.of(
                new BigDecimal("200.50"),
                new BigDecimal("199.50"),
                new BigDecimal("100.00")
        );

        BigDecimal result = calculationEngine.calculateTotalRepayment(repayments);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("500.00")), result);
    }

    @Test
    public void calculateTotalRepaymentWhenWithOnePayment() throws MeedlException {
        List<BigDecimal> repayments = List.of(new BigDecimal("150.75"));

        BigDecimal result = calculationEngine.calculateTotalRepayment(repayments);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("150.75")), result);
    }

    @Test
    public void returnsZeroWhenRepaymentListIsEmpty() throws MeedlException {
        List<BigDecimal> repayments = List.of();

        BigDecimal result = calculationEngine.calculateTotalRepayment(repayments);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("0.00")), result);
    }

    @Test
    public void returnsZeroWhenRepaymentListIsNull() throws MeedlException {
        BigDecimal result = calculationEngine.calculateTotalRepayment(null);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("0.00")), result);
    }
    @Test
    void sortRepaymentsWithValidRecordsSortedInDescendingOrder() throws MeedlException {
        List<RepaymentHistory> repayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 6, 1, 10, 0), new BigDecimal("100")),
                createRepayment(LocalDateTime.of(2025, 6, 3, 9, 0), new BigDecimal("300")),
                createRepayment(LocalDateTime.of(2025, 6, 2, 12, 0), new BigDecimal("200"))
        ));

        List<RepaymentHistory> sorted = calculationEngine.sortRepaymentsByDateTimeAscending(repayments);

        assertEquals(new BigDecimal("300"), sorted.get(2).getAmountPaid());
        assertEquals(new BigDecimal("200"), sorted.get(1).getAmountPaid());
        assertEquals(new BigDecimal("100"), sorted.get(0).getAmountPaid());
    }

    @Test
    void sortRepaymentsWithNull() throws MeedlException {
        List<RepaymentHistory> result = calculationEngine.sortRepaymentsByDateTimeAscending(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void sortRepaymentsWithNullRepayment() {
        List<RepaymentHistory> repayments = new ArrayList<>();
        repayments.add(createRepayment(LocalDateTime.now(), new BigDecimal("100")));
        repayments.add(null);

        MeedlException exception = assertThrows(MeedlException.class,
                () -> calculationEngine.sortRepaymentsByDateTimeAscending(repayments));

        assertEquals(LoanCalculationMessages.REPAYMENT_HISTORY_MUST_BE_PROVIDED.getMessage(), exception.getMessage());
    }

    @Test
    void sortRepaymentsWithNullDate() {
        RepaymentHistory badRepayment = createRepayment(null, new BigDecimal("150"));
        List<RepaymentHistory> repayments = List.of(badRepayment);

        MeedlException exception = assertThrows(MeedlException.class,
                () -> calculationEngine.sortRepaymentsByDateTimeAscending(repayments));

        assertEquals(LoanCalculationMessages.PAYMENT_DATE_CANNOT_BE_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void accumulateTotalRepaidWIthNoPreviousRepayment() throws MeedlException {
        List<RepaymentHistory> repayments = List.of(
                createRepayment(LocalDateTime.of(2025, 1, 1, 10, 0), new BigDecimal("1000")),
                createRepayment(LocalDateTime.of(2025, 2, 1, 10, 0), new BigDecimal("2000")),
                createRepayment(LocalDateTime.of(2025, 3, 1, 10, 0), new BigDecimal("5000"))
        );
//
//<<<<<<< HEAD
//        BigDecimal totalAmountRepaid = loanCalculation.calculateCurrentTotalAmountRepaid(repayments, loaneeId, cohortId);
//=======
//        BigDecimal totalAmountRepaid = calculationEngine.calculateTotalRepayment(repayments, loaneeId, cohortId);
//>>>>>>> 1a1c63d492ab096a7e06f5edfebc079fca10780b
//        log.info("Updated repayment history in test after both sorting \n {}", totalAmountRepaid);
        assertEquals(new BigDecimal("1000"), repayments.get(0).getTotalAmountRepaid());
        assertEquals(new BigDecimal("3000"), repayments.get(1).getTotalAmountRepaid());
        assertEquals(new BigDecimal("8000"), repayments.get(2).getTotalAmountRepaid());
//        assertEquals(new BigDecimal("8000"), totalAmountRepaid);
    }

    @Test
    void accumulateTotalRepaidWithPreviousRepayments() throws MeedlException {
        List<RepaymentHistory> previousRepayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 1, 1, 10, 0), new BigDecimal("1000")),
                createRepayment(LocalDateTime.of(2025, 2, 1, 10, 0), new BigDecimal("2000"))
        ));

        List<RepaymentHistory> newRepayments = new ArrayList<>(List.of(
                createRepayment(LocalDateTime.of(2025, 3, 1, 10, 0), new BigDecimal("3000")),
                createRepayment(LocalDateTime.of(2025, 4, 1, 10, 0), new BigDecimal("4000"))
        ));

        when(repaymentHistoryOutputPort.findAllRepaymentHistoryForLoan(loaneeId, cohortId))
                .thenReturn(previousRepayments);

//<<<<<<< HEAD
//        BigDecimal totalAmountRepaid = loanCalculation.calculateCurrentTotalAmountRepaid(newRepayments, loaneeId, cohortId);
//=======
//        BigDecimal totalAmountRepaid = calculationEngine.calculateTotalRepayment(newRepayments, loaneeId, cohortId);
//>>>>>>> 1a1c63d492ab096a7e06f5edfebc079fca10780b


//        assertEquals(new BigDecimal("10000"), totalAmountRepaid);
    }


}
