package africa.nkwadoma.nkwadoma.domain.service.loanmanagement.loancalculation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
class LoanCalculationServiceTest {
    @Autowired
    private LoanCalculationService calculator;
    String ZERO_WITH_DECIMAL = "0.00";

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
        assertEquals(new BigDecimal(ZERO_WITH_DECIMAL), result);
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
            result = calculator.calculateLoanAmountRequested(
                    BigDecimal.ZERO,
                    new BigDecimal(ZERO_WITH_DECIMAL)
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal(ZERO_WITH_DECIMAL), result);
    }

    @Test
    public void handlesZeroInitialDeposit() {
        BigDecimal result = null;
        try {
            result = calculator.calculateLoanAmountRequested(
                    new BigDecimal("5000.00"),
                    BigDecimal.ZERO
            );
        } catch (MeedlException e) {
            throw new RuntimeException(e);
        }
        assertEquals(new BigDecimal("5000.00"), result);
    }
}
