package africa.nkwadoma.nkwadoma.domain.validation;

import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoanBookValidatorTest {

    @Autowired
    private LoanBookValidator loanBookValidator;

    @BeforeEach
    void setUp() {

    }
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
}