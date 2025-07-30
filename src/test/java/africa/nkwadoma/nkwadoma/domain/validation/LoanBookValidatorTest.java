package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.application.ports.output.education.LoaneeOutputPort;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import africa.nkwadoma.nkwadoma.domain.model.loan.Loanee;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@Slf4j
class LoanBookValidatorTest {

    @InjectMocks
    private LoanBookValidator loanBookValidator;
    @Mock
    private LoaneeOutputPort loaneeOutputPort;
    private final Loanee mockLoanee = new Loanee();

    private final int rowCount = 1;

    @BeforeEach
    public void setUp(){
        loanBookValidator.initializeValidationErrorMessage();
    }

    private Map<String, String> createRow(String key, String value) {
        Map<String, String> row = new HashMap<>();
        row.put(key, value);
        return row;
    }

    @Test
    void validDateddMMyyyy() {
        Map<String, String> row = createRow("startDate", "15-06-2024");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validDateddMMyyyyHHmmss() {
        Map<String, String> row = createRow("startDate", "15-06-2024 10:20:30");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validDateISOLocalDateTime() {
        Map<String, String> row = createRow("startDate", "2024-06-15T10:20:30");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validDateYyyyMd() {
        Map<String, String> row = createRow("startDate", "2024-6-1");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validDatedMYyyy() {
        Map<String, String> row = createRow("startDate", "1-6-2024");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validateDateWithSlashes() {
        Map<String, String> row = createRow("startDate", "15/06/2024");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validateDateWithExtraWhitespaces() {
        Map<String, String> row = createRow("startDate", "  15-06-2024  ");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validateWithNullDate() {
        Map<String, String> row = createRow("startDate", null);
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

    @Test
    void validateEmptyDate() {
        Map<String, String> row = createRow("startDate", "");
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
    }

//    @Test
    void validateInvalidDateFormat() {
        Map<String, String> row = createRow("startDate", "15.06.2024");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
        assertTrue(ex.getMessage().contains("Date doesn't match format"));
    }

//    @Test
    void invalidDayMonth() {
        Map<String, String> row = createRow("startDate", "32-13-2024");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateDateTimeFormat(row, "startDate", rowCount));
        assertTrue(ex.getMessage().contains("Date doesn't match format"));
    }
    @Test
    void validateMoneyValueNullAmount() {
        BigDecimal amount = null;
        String message = "Amount cannot be null";

        MeedlException exception = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateMoneyValue(amount, message, rowCount)
        );

        assertEquals(message, exception.getMessage());
    }

    @Test
    void validateZeroAmountIsValidAmount() {
        BigDecimal amount = BigDecimal.ZERO;
        assertDoesNotThrow(() ->
                loanBookValidator.validateMoneyValue(amount, "Zero is valid", rowCount)
        );
    }


    @Test
    void validateMoneyValueNegativeAmount() {
        BigDecimal amount = new BigDecimal("-50.00");
        String message = "Amount cannot be negative";

        MeedlException exception = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateMoneyValue(amount, message, rowCount)
        );

        assertEquals(message, exception.getMessage());
    }

    @Test
    void validateMoneyValueLargePositiveAmountPassesValidation() {
        BigDecimal amount = new BigDecimal("999999999999.99");
        assertDoesNotThrow(() ->
                loanBookValidator.validateMoneyValue(amount, "Large positive is valid", rowCount)
        );
    }

    @Test
    void testValidateMoneyValueLargeNegativeAmount() {
        BigDecimal amount = new BigDecimal("-999999999999.99");
        String message = "Large negative not allowed";

        MeedlException exception = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateMoneyValue(amount, message, rowCount)
        );

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testValidWholeNumberAmount() {
        Map<String, String> row = createRow("amountPaid", "1000");
        assertDoesNotThrow(() -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
    }

    @Test
    void testValidDecimalAmount() {
        Map<String, String> row = createRow("amountPaid", "1000.50");
        assertDoesNotThrow(() -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
    }

    @Test
    void testAmountWithCommaShouldFail() {
        Map<String, String> row = createRow("amountPaid", "1,000");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }

    @Test
    void validateAmountWithLetter() {
        Map<String, String> row = createRow("amountPaid", "10a00");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }

    @Test
    void validateAmountWithSpecialChars() {
        Map<String, String> row = createRow("amountPaid", "$1000");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }

    @Test
    void validateAmountPaidWithNull() {
        Map<String, String> row = createRow("amountPaid", null);
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }

    @Test
    void validateAmountPaidWithEmptyString() {
        Map<String, String> row = createRow("amountPaid", "");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }

    @Test
    void validateNegativeAmountShould() {
        Map<String, String> row = createRow("amountPaid", "-500");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value. -500"));
    }

    @Test
    void validateForPositiveAmount() {
        Map<String, String> row = createRow("amountPaid", "0");
        assertDoesNotThrow(() -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
    }
    @Test
    void validateAmountWithTrailingDot() {
        Map<String, String> row = createRow("amountPaid", "1000.");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }

    @Test
    void validateAmountWithLeadingDot() {
        Map<String, String> row = createRow("amountPaid", ".50");
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateMonetaryValue(row, "amountPaid", rowCount));
        assertTrue(ex.getMessage().contains("Amount paid is not a monetary value"));
    }



    @Test
    void validateLoaneeExists() throws MeedlException {
        Map<String, String> row = createRow("email", "test@example.com");

        log.info("Mock loanee is {}",mockLoanee);
        when(loaneeOutputPort.findByLoaneeEmail("test@example.com")).thenReturn(mockLoanee);

        assertDoesNotThrow(() ->
                loanBookValidator.validateUserExistByEmail(row.get("email"), rowCount)
        );
    }
//    @Test
    void validateWhenLoaneeDoesNotExist() throws MeedlException {
        Map<String, String> row = createRow("email", "nonexistent@example.com");

        when(loaneeOutputPort.findByLoaneeEmail("nonexistent@example.com")).thenReturn(null);

        MeedlException ex = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateUserExistByEmail(row.get("email"), rowCount)
        );
        assertTrue(ex.getMessage().contains("does not exist for repayment"));
    }
//    @Test
    void validateWithInvalidEmail() throws MeedlException {
        Map<String, String> row = createRow("email", "example.com");

        when(loaneeOutputPort.findByLoaneeEmail("example.com"))
                .thenThrow(new MeedlException("Unexpected error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                loanBookValidator.validateUserExistByEmail(row.get("email"), rowCount)
        );
        assertTrue(ex.getMessage().contains("Unexpected error"));
    }


}