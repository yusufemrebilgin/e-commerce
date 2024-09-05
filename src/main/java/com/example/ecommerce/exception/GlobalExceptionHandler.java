package com.example.ecommerce.exception;

import com.example.ecommerce.exception.response.ErrorDetails;
import com.example.ecommerce.exception.response.SimpleErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleErrorResponse> handleGenericException(Exception ex) {
        log.error("GenericException: {}", ex.getMessage());
        return buildSimpleErrorResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SimpleErrorResponse> handleNotFoundException(Exception ex) {
        log.error("NotFoundException: {}", ex.getMessage());
        return buildSimpleErrorResponse(NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({InvalidPaymentMethodException.class, EmptyCartException.class})
    public ResponseEntity<SimpleErrorResponse> handleBadRequestException(Exception ex) {
        log.error("BadRequestException: {}", ex.getMessage());
        return buildSimpleErrorResponse(BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({UsernameAlreadyTakenException.class, EmailAlreadyInUseException.class})
    public ResponseEntity<SimpleErrorResponse> handleConflictException(Exception ex) {
        log.error("Conflict: {}", ex.getMessage());
        return buildSimpleErrorResponse(CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenRoleAssignmentException.class)
    public ResponseEntity<SimpleErrorResponse> handleForbiddenException(Exception ex) {
        log.error("Forbidden: {}", ex.getMessage());
        return buildSimpleErrorResponse(FORBIDDEN, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log.error("Validation failed: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorDetails errorDetails = ErrorDetails.builder()
                .status(BAD_REQUEST.toString())
                .message("Validation Failed")
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    private ResponseEntity<SimpleErrorResponse> buildSimpleErrorResponse(HttpStatusCode status, String message) {
        return new ResponseEntity<>(
                SimpleErrorResponse.builder().message(message).status(status.toString()).build(),
                status
        );
    }

}
