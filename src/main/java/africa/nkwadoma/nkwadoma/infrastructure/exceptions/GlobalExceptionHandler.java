package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import com.fasterxml.jackson.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, ExceptionResponse>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, ExceptionResponse> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorResponseBuilder(errorMessage));
        });
        log.info("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(IdentityVerificationException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(IdentityVerificationException ex) {
        return new ResponseEntity<>(errorResponseBuilder(ex.getMessage()), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleEnumValidationExceptions(HttpMessageNotReadableException ex) {
        log.error("Error validating input: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponseBuilder(ErrorMessages.INVALID_INPUT_PROVIDED + ": " + ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MeedlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<ExceptionResponse> handleMeedlException(MeedlException exception){
        return new ResponseEntity<>(errorResponseBuilder(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(InvestmentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<ExceptionResponse> handleInvestmentException(InvestmentException exception){
        return new ResponseEntity<>(errorResponseBuilder(exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<ExceptionResponse> handleSortInputException(PropertyReferenceException exception){
        log.info("Invalid sort by parameter: {}", exception.getMessage());
        return new ResponseEntity<>(errorResponseBuilder(ErrorMessages.INVALID_SORT_PARAMETER), HttpStatus.BAD_REQUEST);
    }

    private static ExceptionResponse errorResponseBuilder(String message) {
        return ExceptionResponse.builder()
                .message(message)
                .timeStamp(LocalDateTime.now())
                .statusCode(HttpStatus.BAD_REQUEST.name())
                .build();
    }
}
