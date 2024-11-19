package com.example.ecommerce.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<SimpleErrorResponse> handleApplicationException(ApplicationException ex, WebRequest request) {
        // Logging the request details, current user, status and exception message with stack trace
        log.error(
                "Exception occurred at endpoint: {} - User: {} - Status: {} - Error: {}",
                request.getDescription(false),
                getCurrentUser(),
                ex.getStatus(),
                ex.getMessage(),
                ex
        );
        return buildSimpleErrorResponse(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<SimpleErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.error("Access denied. User `{}` tried to access: {}", getCurrentUser(), request.getDescription(false));
        return buildSimpleErrorResponse(FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SimpleErrorResponse> handleGenericException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return buildSimpleErrorResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(@NonNull MaxUploadSizeExceededException ex,
                                                                          @NonNull HttpHeaders headers,
                                                                          @NonNull HttpStatusCode status,
                                                                          @NonNull WebRequest request) {

        log.error("File size exceeds the maximum allowed limit for current request");
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(PAYLOAD_TOO_LARGE, ex.getMessage());
        return new ResponseEntity<>(errorResponse, PAYLOAD_TOO_LARGE);
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

        ErrorDetails errorDetails = new ErrorDetails(
                BAD_REQUEST,
                "Validation Failed",
                validationErrors
        );

        return new ResponseEntity<>(errorDetails, BAD_REQUEST);
    }

    private ResponseEntity<SimpleErrorResponse> buildSimpleErrorResponse(HttpStatus status, String message) {
        return new ResponseEntity<>(
                new SimpleErrorResponse(status, message),
                status
        );
    }

    private String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return principal.toString();
            }
        }
        return "Anonymous"; // Return "Anonymous" for unauthenticated users
    }

}
