package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanBookValidatorTest {

    @Autowired
    private LoanBookValidator loanBookValidator;

    private Map<String, String> createRow(String key, String value) {
        Map<String, String> row = new HashMap<>();
        row.put(key, value);
        return row;
    }

    @Test
    void testValidDateddMMyyyy() {
        List<Map<String, String>> data = List.of(createRow("startDate", "15-06-2024"));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testValidDateddMMyyyyHHmmss() {
        List<Map<String, String>> data = List.of(createRow("startDate", "15-06-2024 10:20:30"));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testValidDateISOLocalDateTime() {
        List<Map<String, String>> data = List.of(createRow("startDate", "2024-06-15T10:20:30"));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testValidDate_yyyyMd() {
        List<Map<String, String>> data = List.of(createRow("startDate", "2024-6-1"));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testValidDate_dMyyyy() {
        List<Map<String, String>> data = List.of(createRow("startDate", "1-6-2024"));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testDateWithSlashes() {
        List<Map<String, String>> data = List.of(createRow("startDate", "15/06/2024"));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testDateWithExtraWhitespaces() {
        List<Map<String, String>> data = List.of(createRow("startDate", "  15-06-2024  "));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testNullDate() {
        List<Map<String, String>> data = List.of(createRow("startDate", null));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testEmptyDate() {
        List<Map<String, String>> data = List.of(createRow("startDate", ""));
        assertDoesNotThrow(() -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
    }

    @Test
    void testInvalidDateFormat() {
        List<Map<String, String>> data = List.of(createRow("startDate", "15.06.2024"));
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
        assertTrue(ex.getMessage().contains("Date doesn't match format"));
    }

    @Test
    void testCompletelyInvalidDate() {
        List<Map<String, String>> data = List.of(createRow("startDate", "this-is-not-a-date"));
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
        assertTrue(ex.getMessage().contains("Date doesn't match format"));
    }

    @Test
    void testInvalidDayMonth() {
        List<Map<String, String>> data = List.of(createRow("startDate", "32-13-2024"));
        MeedlException ex = assertThrows(MeedlException.class,
                () -> loanBookValidator.validateDateTimeFormat(data, "startDate"));
        assertTrue(ex.getMessage().contains("Date doesn't match format"));
    }
    @Test
    void testValidateMoneyValueNullAmount() {
        BigDecimal amount = null;
        String message = "Amount cannot be null";

        MeedlException exception = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateMoneyValue(amount, message)
        );

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testValidateMoneyValueZeroAmountPassesValidation() {
        BigDecimal amount = BigDecimal.ZERO;
        assertDoesNotThrow(() ->
                loanBookValidator.validateMoneyValue(amount, "Zero is valid")
        );
    }

    @Test
    void testValidateMoneyValuePositiveAmountPassesValidation() {
        BigDecimal amount = new BigDecimal("100.00");
        assertDoesNotThrow(() ->
                loanBookValidator.validateMoneyValue(amount, "Positive is valid")
        );
    }

    @Test
    void testValidateMoneyValueNegativeAmount() {
        BigDecimal amount = new BigDecimal("-50.00");
        String message = "Amount cannot be negative";

        MeedlException exception = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateMoneyValue(amount, message)
        );

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testValidateMoneyValueLargePositiveAmount_passesValidation() {
        BigDecimal amount = new BigDecimal("999999999999.99");
        assertDoesNotThrow(() ->
                loanBookValidator.validateMoneyValue(amount, "Large positive is valid")
        );
    }

    @Test
    void testValidateMoneyValueLargeNegativeAmount() {
        BigDecimal amount = new BigDecimal("-999999999999.99");
        String message = "Large negative not allowed";

        MeedlException exception = assertThrows(MeedlException.class, () ->
                loanBookValidator.validateMoneyValue(amount, message)
        );

        assertEquals(message, exception.getMessage());
    }
}