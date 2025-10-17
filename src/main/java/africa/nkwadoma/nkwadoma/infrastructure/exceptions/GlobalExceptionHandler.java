package africa.nkwadoma.nkwadoma.infrastructure.exceptions;

import africa.nkwadoma.nkwadoma.domain.exceptions.*;
import africa.nkwadoma.nkwadoma.infrastructure.adapters.input.rest.message.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
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
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.info("Validation errors: {}", errorMessages);
        return new ResponseEntity<>(errorResponseBuilder(errorMessages), HttpStatus.BAD_REQUEST);
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
