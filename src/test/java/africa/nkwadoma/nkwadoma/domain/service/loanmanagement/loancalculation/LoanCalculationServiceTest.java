package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.loanBook.LoanPeriodRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoanCalculationServiceTest {
    @Autowired
    private LoanCalculationService calculator;
    BigDecimal ZERO = new BigDecimal("0.00");

    @BeforeEach
    void setUp() {
    }

    @Test
    public void calculatesCorrectLoanAmount() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountRequested(
                    new BigDecimal("10000.00"),
                    new BigDecimal("2500.00")
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("-500.00"), result);
    }

    @Test
    public void throwsExceptionWhenProgramFeeIsNull() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(null, new BigDecimal("1000.00"))
        );
        assertEquals("Program Fee must not be null.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenInitialDepositIsNull() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(new BigDecimal("1000.00"), null)
        );
        assertEquals("Initial Deposit must not be null.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenProgramFeeIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(new BigDecimal("-1000.00"), new BigDecimal("200.00"))
        );
        assertEquals("Program Fee must not be negative.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenInitialDepositIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountRequested(new BigDecimal("2000.00"), new BigDecimal("-300.00"))
        );
        assertEquals("Initial Deposit must not be negative.", exception.getMessage());
    }

    @Test
    public void handlesZeroProgramFee() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountRequested(ZERO, ZERO);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(ZERO, result);
    }

    @Test
    public void handlesZeroInitialDeposit() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountRequested(
                    new BigDecimal("5000.00"),
                    ZERO
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("5000.00"), result);
    }


    @Test
    public void calculatesCorrectLoanDisbursedOffered() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanDisbursedOffered(
                    new BigDecimal("7000.00"),
                    new BigDecimal("500.00")
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("7500.00"), result);
    }

    @Test
    public void returnsSameAsLoanAmountWhenFeesAreZero() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanDisbursedOffered(
                    new BigDecimal("6000.00"),
                    ZERO
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("6000.00"), result);
    }

    @Test
    public void returnsOnlyFeesWhenLoanAmountIsZero() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanDisbursedOffered(
                    ZERO,
                    new BigDecimal("200.00")
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("200.00"), result);
    }

    @Test
    public void returnsZeroWhenBothInputsAreZero() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanDisbursedOffered(ZERO, ZERO);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(ZERO, result);
    }

    @Test
    public void throwsExceptionWhenLoanAmountRequestedIsNull() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanDisbursedOffered(null, new BigDecimal("100.00"))
        );
        assertEquals("Loan Amount Requested must not be null.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenLoanDisbursementFeesIsNull() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanDisbursedOffered(new BigDecimal("8000.00"), null)
        );
        assertEquals("Loan Disbursement Fees must not be null.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenLoanAmountRequestedIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanDisbursedOffered(new BigDecimal("-3000.00"), new BigDecimal("100.00"))
        );
        assertEquals("Loan Amount Requested must not be negative.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenLoanDisbursementFeesIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanDisbursedOffered(new BigDecimal("4000.00"), new BigDecimal("-100.00"))
        );
        assertEquals("Loan Disbursement Fees must not be negative.", exception.getMessage());
    }



    @Test
    public void calculatesMonthlyInterestCorrectly() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(60);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(5, result);
    }

    @Test
    public void handlesInterestRateOfZero() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(0);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, result);
    }

    @Test
    public void handlesInterestRateOfOne() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(1);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, result); // 1 / 12 = 0 in int division
    }

    @Test
    public void handlesInterestRateOfTwelve() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(12);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1, result);
    }

    @Test
    public void handlesInterestRateOfOneHundred() {
        int result = 0;
        try {
            result = calculator.calculateMonthlyInterestRate(100);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(8, result);
    }

    @Test
    public void throwsExceptionWhenInterestRateIsNegative() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateMonthlyInterestRate(-1)
        );
        assertEquals("Interest rate must not be negative.", exception.getMessage());
    }

    @Test
    public void throwsExceptionWhenInterestRateIsGreaterThanHundred() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                calculator.calculateMonthlyInterestRate(101)
        );
        assertEquals("Interest rate must not exceed 100.", exception.getMessage());
    }


    @Test
    public void calculatesCorrectOutstandingAmount() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountOutstanding(
                    new BigDecimal("10000.00"),
                    new BigDecimal("1500.00"),
                    50
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("8550.00"), result);
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
            throw new RuntimeException(e);
        }
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    public void allowsFullInterestOfOneHundred() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountOutstanding(
                    new BigDecimal("3000.00"),
                    new BigDecimal("500.00"),
                    100
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("2600.00"), result);
    }

    @Test
    public void throwsExceptionWhenLoanAmountOutstandingIsNegative() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("-1"), new BigDecimal("100"), 10)
        );
        assertEquals("Loan Amount Outstanding must not be negative.", ex.getMessage());
    }

    @Test
    public void throwsExceptionWhenMonthlyRepaymentIsNegative() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("100"), new BigDecimal("-1"), 10)
        );
        assertEquals("Monthly Repayment must not be negative.", ex.getMessage());
    }

    @Test
    public void throwsExceptionWhenLoanAmountOutstandingIsNull() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(null, new BigDecimal("500"), 20)
        );
        assertEquals("Loan Amount Outstanding must not be null.", ex.getMessage());
    }

    @Test
    public void throwsExceptionWhenMonthlyRepaymentIsNull() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("500"), null, 20)
        );
        assertEquals("Monthly Repayment must not be null.", ex.getMessage());
    }

    @Test
    public void throwsExceptionWhenInterestIsNegative() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("1000"), new BigDecimal("500"), -5)
        );
        assertEquals("Money Weighted Periodic Interest must not be negative.", ex.getMessage());
    }

    @Test
    public void throwsExceptionWhenInterestIsAboveHundred() {
        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateLoanAmountOutstanding(new BigDecimal("1000"), new BigDecimal("500"), 101)
        );
        assertEquals("Money Weighted Periodic Interest must not exceed 100.", ex.getMessage());
    }



    @Test
    public void calculatesWeightedInterestCorrectly() {
        List<LoanPeriodRecord> records = Arrays.asList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10),
                new LoanPeriodRecord(new BigDecimal("2000.00"), 5)
        );

        BigDecimal result = null;
        try {
            result = calculator.calculateMoneyWeightedPeriodicInterest(10, records);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }

        // Expected: (10 / 365) * ((1000*10) + (2000*5)) = (10 / 365) * (10000 + 10000) = (10 / 365) * 20000
        BigDecimal expected = new BigDecimal("10")
                .divide(new BigDecimal("365"), 10, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("20000"));

        assertEquals(0, result.compareTo(expected));
    }

    @Test
    public void handlesZeroInterestRate() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1500.00"), 30)
        );

        BigDecimal result = null;
        try {
            result = calculator.calculateMoneyWeightedPeriodicInterest(0, records);
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, result.compareTo(BigDecimal.ZERO));

    }

    @Test
    public void handlesEmptyRecordList() {
        BigDecimal result = null;
        try {
            result = calculator.calculateMoneyWeightedPeriodicInterest(5, Collections.emptyList());
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(0, result.compareTo(BigDecimal.ZERO));

    }

    @Test
    public void throwsExceptionWhenInterestRateIsNegativeForLoanPeriodRecord() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10));

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(-1, records)
        );
        assertEquals("Interest rate must not be negative.", ex.getMessage());
    }

    @Test
    public void throwsExceptionWhenInterestRateExceeds100() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("1000.00"), 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(101, records)
        );
        assertEquals("Interest rate must not exceed 100.", ex.getMessage());
    }

    @Test
    public void throwsExceptionForNegativeLoanAmountOutstanding() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("-100.00"), 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(100, records)
        );
    }

    @Test
    public void throwsExceptionForNegativeDaysHeld() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(new BigDecimal("100.00"), -10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(100, records));
    }

    @Test
    public void throwsExceptionWhenLoanAmountOutstandingIsNullForLoanPeriodRecord() {
        List<LoanPeriodRecord> records = Collections.singletonList(
                new LoanPeriodRecord(null, 10)
        );

        MeedlException ex = assertThrows(MeedlException.class, () ->
                calculator.calculateMoneyWeightedPeriodicInterest(100, records));
    }
}
