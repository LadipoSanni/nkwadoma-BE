package africa.nkwadoma.nkwadoma.domain.validation;

import static org.junit.jupiter.api.Assertions.*;

import africa.nkwadoma.nkwadoma.domain.enums.constants.MeedlMessages;
import africa.nkwadoma.nkwadoma.domain.enums.constants.loan.LoanMessages;
import africa.nkwadoma.nkwadoma.domain.exceptions.MeedlException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

class MeedlValidatorTest {

    @Test
    void validateEmail_shouldThrowExceptionForInvalidEmail() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateEmail("invalid-email")
        );
        assertEquals(MeedlMessages.INVALID_EMAIL_ADDRESS.getMessage(), exception.getMessage());
    }

    @Test
    void validateEmail_shouldPassForValidEmail() {
        assertDoesNotThrow(() -> MeedlValidator.validateEmail("test@example.com"));
    }

    @Test
    void validateLoanDecision_shouldThrowExceptionForInvalidDecision() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateLoanDecision("PENDING")
        );
        assertEquals(LoanMessages.INVALID_LOAN_DECISION.getMessage(), exception.getMessage());
    }

    @Test
    void validateLoanDecision_shouldPassForValidDecision() {
        assertDoesNotThrow(() -> MeedlValidator.validateLoanDecision("ACCEPTED"));
        assertDoesNotThrow(() -> MeedlValidator.validateLoanDecision("DECLINED"));
    }

    @Test
    void validateUUID_shouldThrowExceptionForInvalidUUID() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateUUID("invalid-uuid", "Invalid UUID format")
        );
        assertEquals("Invalid UUID format", exception.getMessage());
    }

    @Test
    void validateUUID_shouldPassForValidUUID() {
        assertDoesNotThrow(() -> MeedlValidator.validateUUID(UUID.randomUUID().toString(), "Valid UUID"));
    }

    @Test
    void validateBigDecimalDataElement_shouldThrowExceptionForNull() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateBigDecimalDataElement(null)
        );
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    void validateBigDecimalDataElement_shouldPassForValidValue() {
        assertDoesNotThrow(() -> MeedlValidator.validateBigDecimalDataElement(BigDecimal.valueOf(100.00)));
    }

    @Test
    void validateBvn_shouldThrowExceptionForInvalidBvn() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateBvn("123456")
        );
        assertEquals("Invalid bvn provided", exception.getMessage());
    }

    @Test
    void validateBvn_shouldPassForValidBvn() {
        assertDoesNotThrow(() -> MeedlValidator.validateBvn("12345678901"));
    }

    @Test
    void validateAccountNumber_shouldThrowExceptionForShortAccountNumber() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateAccountNumber("12345", "Invalid account number")
        );
        assertEquals("Bank account number cannot be less than ten or greater than fifteen", exception.getMessage());
    }

    @Test
    void validateAccountNumber_shouldPassForValidAccountNumber() {
        assertDoesNotThrow(() -> MeedlValidator.validateAccountNumber("1234567890", "Valid account number"));
    }
}
