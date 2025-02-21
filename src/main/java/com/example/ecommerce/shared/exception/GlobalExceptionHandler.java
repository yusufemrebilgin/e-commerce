package com.example.ecommerce.shared.exception;

import com.example.ecommerce.shared.payload.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

/**
 * GlobalExceptionHandler is a centralized exception handler for the application.
 * It handles various types of exceptions thrown during request processing.
 * <p>
 * For each exception, the handler logs detailed information about the error, including the request details,
 * the authenticated user (if available), status code, exception message, and the stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex, WebRequest request) {
        // Logging the request details, current username, status and exception message with stack trace
        logger.error(
                "Exception occurred at endpoint: '{}' - User: '{}' - Status: '{}' - Error: '{}'",
                request.getDescription(false),
                getCurrentUsername(),
                ex.getStatus(),
                ex.getMessage(),
                ex
        );

        return buildApplicationErrorResponse(ex, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {

        logger.error(
                "Unauthorized access attempt by user '{}' to '{}' - Error: '{}'",
                getCurrentUsername(),
                request.getDescription(false),
                ex.getMessage(),
                ex
        );

        return buildErrorResponse(UNAUTHORIZED, ex, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {

        logger.error(
                "User '{}' attempted to unauthorized access to '{}' - Error: '{}'",
                getCurrentUsername(),
                request.getDescription(false),
                ex.getMessage(),
                ex
        );

        return buildErrorResponse(FORBIDDEN, ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {

        logger.error(
                "An unexpected error occurred at endpoint '{}' for user '{}' - Error: '{}'",
                request.getDescription(false),
                getCurrentUsername(),
                ex.getMessage(),
                ex
        );

        return buildErrorResponse(INTERNAL_SERVER_ERROR, ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            @NonNull MaxUploadSizeExceededException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {

        logger.error(
                "File size exceeds the maximum allowed limit for current endpoint: '{}' - User: '{}' - Error: '{}'",
                request.getDescription(false),
                getCurrentUsername(),
                ex.getMessage(),
                ex
        );

        ErrorResponse errorResponse = ErrorResponse.of(
                ex,
                PAYLOAD_TOO_LARGE,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, PAYLOAD_TOO_LARGE);

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {

        logger.error(
                "Validation failed at endpoint: '{}' - User: '{}' - Status: '{}' - Error: '{}'",
                request.getDescription(false),
                getCurrentUsername(),
                BAD_REQUEST,
                ex.getClass().getSimpleName(),
                ex
        );

        String timestamp = Instant.now().toString();

        List<ErrorResponse> errors = ex.getAllErrors().stream()
                .map(error -> new ErrorResponse(
                        null,
                        String.format("Validation failed for field [%s]", ((FieldError) error).getField()),
                        error.getDefaultMessage(),
                        request.getDescription(false),
                        timestamp
                )).toList();

        return new ResponseEntity<>(Map.of("errors", errors), BAD_REQUEST);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception ex, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.of(ex, status, request.getDescription(false)), status);
    }

    private ResponseEntity<ErrorResponse> buildApplicationErrorResponse(ApplicationException ex, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.of(ex, request), ex.getStatus());
    }

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username if authenticated, otherwise "anonymousUser"
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymousUser";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return "anonymousUser";
    }

}
