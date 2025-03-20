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
    void validateEmailShouldThrowExceptionForInvalidEmail() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateEmail("invalid-email")
        );
        assertEquals(MeedlMessages.INVALID_EMAIL_ADDRESS.getMessage(), exception.getMessage());
    }

    @Test
    void validateEmailShouldPassForValidEmail() {
        assertDoesNotThrow(() -> MeedlValidator.validateEmail("test@example.com"));
    }

    @Test
    void validateLoanDecisionShouldThrowExceptionForInvalidDecision() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateLoanDecision("PENDING")
        );
        assertEquals(LoanMessages.INVALID_LOAN_DECISION.getMessage(), exception.getMessage());
    }

    @Test
    void validateLoanDecisionShouldPassForValidDecision() {
        assertDoesNotThrow(() -> MeedlValidator.validateLoanDecision("ACCEPTED"));
        assertDoesNotThrow(() -> MeedlValidator.validateLoanDecision("DECLINED"));
    }

    @Test
    void validateUUIDShouldThrowExceptionForInvalidUUID() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateUUID("invalid-uuid", "Invalid UUID format")
        );
        assertEquals("Invalid UUID format", exception.getMessage());
    }

    @Test
    void validateUUIDShouldPassForValidUUID() {
        assertDoesNotThrow(() -> MeedlValidator.validateUUID(UUID.randomUUID().toString(), "Valid UUID"));
    }

    @Test
    void validateBigDecimalDataElementShouldThrowExceptionForNull() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateBigDecimalDataElement(null)
        );
        assertEquals(MeedlMessages.EMPTY_INPUT_FIELD_ERROR.getMessage(), exception.getMessage());
    }

    @Test
    void validateBigDecimalDataElementShouldPassForValidValue() {
        assertDoesNotThrow(() -> MeedlValidator.validateBigDecimalDataElement(BigDecimal.valueOf(100.00)));
    }

    @Test
    void validateBvnShouldThrowExceptionForInvalidBvn() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateBvn("123456")
        );
        assertEquals("Invalid bvn provided", exception.getMessage());
    }

    @Test
    void validateBvnShouldPassForValidBvn() {
        assertDoesNotThrow(() -> MeedlValidator.validateBvn("12345678901"));
    }

    @Test
    void validateAccountNumberShouldThrowExceptionForShortAccountNumber() {
        MeedlException exception = assertThrows(MeedlException.class, () ->
                MeedlValidator.validateAccountNumber("12345", "Invalid account number")
        );
        assertEquals("Bank account number cannot be less than ten or greater than fifteen", exception.getMessage());
    }

    @Test
    void validateAccountNumberShouldPassForValidAccountNumber() {
        assertDoesNotThrow(() -> MeedlValidator.validateAccountNumber("1234567890", "Valid account number"));
    }
}
