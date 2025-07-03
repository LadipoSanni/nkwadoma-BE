package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.application.ports.input.loanmanagement.loancalculation.LoanCalculationUseCase;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoanCalculationServiceTest {
    @Autowired
    private LoanCalculationUseCase calculator;
    private final BigDecimal ZERO = new BigDecimal("0.00");
    private final int NUMBER_OF_DECIMAL_PLACE = 8;


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
            result = calculator.calculateLoanAmountRequested(
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
            result = calculator.calculateLoanAmountRequested(
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
            result = calculator.calculateLoanAmountRequested(
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
                calculator.calculateLoanAmountRequested(null, new BigDecimal("1000.00"))
        );
        assertEquals("Program Fee must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedWithNullInitialDeposit() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(new BigDecimal("1000.00"), null)
        );
        assertEquals("Initial Deposit must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedWithNegativeProgramFee() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(new BigDecimal("-1000.00"), new BigDecimal("200.00"))
        );
        assertEquals("Program Fee must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedWithNegativeInitialDeposit() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(new BigDecimal("2000.00"), new BigDecimal("-300.00"))
        );
        assertEquals("Initial Deposit must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountRequestedAllowsZeroProgramFeeAndInitialDeposit() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountRequested(ZERO, ZERO);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(ZERO, result);
    }



    @Test
    public void calculateLoanAmountDisbursed() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountDisbursed(
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
                calculator.calculateLoanAmountDisbursed(null, new BigDecimal("100.00"))
        );
        assertEquals("Loan Amount Requested must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountDisbursedWithNullDisbursementFees() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountDisbursed(new BigDecimal("8000.00"), null)
        );
        assertEquals("Loan Disbursement Fees must not be null.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountDisbursedWithNegativeAmountRequested() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountDisbursed(new BigDecimal("-3000.00"), new BigDecimal("100.00"))
        );
        assertEquals("Loan Amount Requested must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateLoanAmountDisbursedWithNegativeDisbursementFees() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountDisbursed(new BigDecimal("4000.00"), new BigDecimal("-100.00"))
        );
        assertEquals("Loan Disbursement Fees must not be negative.", exception.getMessage());
    }



    @Test
    public void calculatesMonthlyInterest() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(60);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(5, result);
    }

    @Test
    public void handlesInterestRateOfZero() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(0);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(0, result);
    }

    @Test
    public void calculatesInterestRateOfOne() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(1);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(0, result);
    }

    @Test
    public void calculatesInterestRateOfTwelve() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(12);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(1, result);
    }

    @Test
    public void calculatesInterestRateOfOneHundred() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(100);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(8, result);
    }

    @Test
    public void calculateInterestRateIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateMonthlyInterestRate(-1)
        );
        assertEquals("Interest rate must not be negative.", exception.getMessage());
    }

    @Test
    public void calculateInterestRateGreaterThanHundred() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateMonthlyInterestRate(101)
        );
        assertEquals("Interest rate must not exceed 100.", exception.getMessage());
    }


    @Test
    public void calculateOutstandingAmount() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountOutstanding(
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
            result = calculator.calculateLoanAmountOutstanding(
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
                calculator.calculateLoanAmountOutstanding(new BigDecimal("-1"), new BigDecimal("100"), 10)
        );
        assertEquals("Loan Amount Outstanding must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateLoanAmountOutstandingWithNegativeMonthlyRepayment() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("100"), new BigDecimal("-1"), 10)
        );
        assertEquals("Monthly Repayment must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateLoanAmountOutstandingWithNullAmountOutstanding() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(null, new BigDecimal("500"), 20)
        );
        assertEquals("Loan Amount Outstanding must not be null.", ex.getMessage());
    }

    @Test
    public void calculateLoanAmountOutstandingWithNullMonthlyRepayment() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("500"), null, 20)
        );
        assertEquals("Monthly Repayment must not be null.", ex.getMessage());
    }

    @Test
    public void calculateAmountOutstandingWithNegativeMoneyWeightedPeriodicInterest() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("1000"), new BigDecimal("500"), -5)
        );
        assertEquals("Money Weighted Periodic Interest must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateAmountOutstandingWithInterestAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("1000"), new BigDecimal("500"), 101)
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
            result = calculator.calculateMoneyWeightedPeriodicInterest(10, records);
        } catch (MeedlException e) {
            log.error("",e);
        }

        // For easy understanding.
        // Interest = 10
        // Interest / 365 = 10/363 = 0.0274
        // Summation of Loan period record = ((1000*10) + (2000*5)) = (10000 + 10000) = 20000
        // 0.0274 * 20000 = 547.94520
        // Expected: (10 / 365) * ((1000*10) + (2000*5)) = (10 / 365) * (10000 + 10000) = (10 / 365) * 20000
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
            result = calculator.calculateMoneyWeightedPeriodicInterest(0, records);
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
            result = calculator.calculateMoneyWeightedPeriodicInterest(5, Collections.emptyList());
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
                calculator.calculateMoneyWeightedPeriodicInterest(-1, records)
        );
        assertEquals("Interest rate must not be negative.", ex.getMessage());
    }

    @Test
    public void calculatePeriodicInterestWithInterestRateAbove100() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(101, records)
        );
        assertEquals("Interest rate must not exceed 100.", ex.getMessage());
    }

    @Test
    public void calculatePeriodicInterestWithNegativeInterestRate() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("-100.00"), 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(100, records)
        );
    }

    @Test
    public void calculateMoneyWeightedPeriodicInterestWithNegativeDaysHeld() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("100.00"), -10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(100, records));
    }

    @Test
    public void calculateMoneyWeightedPeriodicInterestWithNullAmountOutstanding() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(null, 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(100, records));
    }



    @Test
    public void calculateManagementFee() {
        BigDecimal result = null;
        try {
            result = calculator.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), 5.5);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("550.00000000"), result);
    }

    @Test
    public void calculationReturnsZeroWhenPercentageIsZero() {
        BigDecimal result = null;
        try {
            result = calculator.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), 0);
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
            result = calculator.calculateManagementOrProcessingFee(new BigDecimal("2000.00"), 100);
        } catch (MeedlException e) {
            log.error("",e);
        }
        assertEquals(new BigDecimal("2000.00000000"), result);
    }

    @Test
    public void calculateManagementFeeWithNulLoanAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateManagementOrProcessingFee(null, 10)
        );
        assertEquals("Loan Amount Requested must not be null.", ex.getMessage());
    }

    @Test
    public void calculateManagementFeeWithNegativeLoanAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateManagementOrProcessingFee(new BigDecimal("-5000.00"), 10)
        );
        assertEquals("Loan Amount Requested must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateManagementFeeWithNegativeMgtFeeInPercent() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), -1)
        );
        assertEquals("Management Fee Percentage must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateManagementFeeWithMgtFeeInPercentAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateManagementOrProcessingFee(new BigDecimal("10000.00"), 101)
        );
        assertEquals("Management Fee Percentage must not exceed 100.", ex.getMessage());
    }




    @Test
    public void calculateCreditLifeCorrectlyForOneYearTenure() throws MeedlException {
        BigDecimal result = calculator.calculateCreditLife(new BigDecimal("10000.00"), 2, 12);
        assertEquals(new BigDecimal("200.00000000"), result);
    }

    @Test
    public void calculateCreditLifeForMoreThanOneYearTenure() throws MeedlException {
        BigDecimal result = calculator.calculateCreditLife(new BigDecimal("10000.00"), 2, 14);
        assertEquals(new BigDecimal("400.00000000"), result);
    }

    @Test
    public void calculateCreditLifeDefaultsToOneYearIfLessThanTwelveMonths() throws MeedlException {
        BigDecimal result = calculator.calculateCreditLife(new BigDecimal("5000.00"), 3, 5);
        assertEquals(new BigDecimal("150.00000000"), result);
    }

    @Test
    public void calculateCreditLifeReturnsZeroForZeroPercentage() throws MeedlException {
        BigDecimal result = calculator.calculateCreditLife(new BigDecimal("8000.00"), 0, 24);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    public void calculateCreditLifeWithNegativeAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateCreditLife(new BigDecimal("-5000.00"), 2, 12)
        );
        assertEquals("Loan Amount Requested must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeWithNullAmountRequested() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateCreditLife(null, 2, 12)
        );
        assertEquals("Loan Amount Requested must not be null.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeWithNegativePercentage() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateCreditLife(new BigDecimal("10000.00"), -5, 12)
        );
        assertEquals("Credit Life Percentage must not be negative.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeForPercentageAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateCreditLife(new BigDecimal("10000.00"), 101, 12)
        );
        assertEquals("Credit Life Percentage must not exceed 100.", ex.getMessage());
    }

    @Test
    public void calculateCreditLifeWithNegativeLoanTenure() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateCreditLife(new BigDecimal("10000.00"), 5, -6)
        );
        assertEquals("Loan Tenure must not be negative.", ex.getMessage());
    }



    @Test
    public void calculateDisbursementFees() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of(
                "Credit Life", new BigDecimal("100.00"),
                "Management Fee", new BigDecimal("200.00")
        );

        BigDecimal result = calculator.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("300.00")), result);
    }

    @Test
    public void calculateDisbursementFeesCorrectlyWhenOneEntryIsProvided() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of("Credit Life", new BigDecimal("75.00"));

        BigDecimal result = calculator.calculateLoanDisbursementFees(fees);
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

        BigDecimal result = calculator.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("125.00")), result);
    }

    @Test
    public void returnsZeroWhenNoFeesProvided() throws MeedlException {
        Map<String, BigDecimal> fees = Map.of();
        BigDecimal result = calculator.calculateLoanDisbursementFees(fees);
        assertEquals(decimalPlaceRoundUp(BigDecimal.ZERO), result);
    }

    @Test
    public void calculateDisbursementFeesWithNegativeProcessingFee() {
        Map<String, BigDecimal> fees = Map.of("Processing Fee", new BigDecimal("-50.00"));

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanDisbursementFees(fees)
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

        BigDecimal result = calculator.calculateTotalRepayment(repayments);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("500.00")), result);
    }

    @Test
    public void calculateTotalRepaymentWhenWithOnePayment() throws MeedlException {
        List<BigDecimal> repayments = List.of(new BigDecimal("150.75"));

        BigDecimal result = calculator.calculateTotalRepayment(repayments);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("150.75")), result);
    }

    @Test
    public void returnsZeroWhenRepaymentListIsEmpty() throws MeedlException {
        List<BigDecimal> repayments = List.of();

        BigDecimal result = calculator.calculateTotalRepayment(repayments);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("0.00")), result);
    }

    @Test
    public void returnsZeroWhenRepaymentListIsNull() throws MeedlException {
        BigDecimal result = calculator.calculateTotalRepayment(null);
        assertEquals(decimalPlaceRoundUp(new BigDecimal("0.00")), result);
    }
}
