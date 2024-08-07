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
    public ResponseEntity<SimpleErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("GenericException: {}", ex.getMessage(), ex);
        SimpleErrorResponse errorResponse = SimpleErrorResponse.builder()
                .status(INTERNAL_SERVER_ERROR.toString())
                .message("An unexpected error occurred: " + ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<SimpleErrorResponse> handleNotFoundException(Exception ex, WebRequest request) {
        log.error("NotFoundException: {}", ex.getMessage(), ex);
        SimpleErrorResponse errorResponse = SimpleErrorResponse.builder()
                .status(NOT_FOUND.toString())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler({UsernameAlreadyTakenException.class, EmailAlreadyInUseException.class})
    public ResponseEntity<SimpleErrorResponse> handleBadRequestException(Exception ex, WebRequest request) {
        log.error("BadRequestException: {}", ex.getMessage(), ex);
        SimpleErrorResponse errorResponse = SimpleErrorResponse.builder()
                .status(BAD_REQUEST.toString())
                .message(ex.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        log.error("Validation failed: {}", ex.getMessage(), ex);

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

}
