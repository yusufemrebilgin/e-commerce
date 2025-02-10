package com.example.ecommerce.shared.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for application-specific exceptions.
 * <p>
 * This abstract class provides a common structure for all exceptions in the application
 * by associating each exception with an {@link HttpStatus} code and an error message.
 * It serves as the foundation for specific exceptions such as:
 * <ul>
 *   <li>{@link BadRequestException} - For HTTP 400 errors (invalid client input).</li>
 *   <li>{@link UnauthorizedException} - For HTTP 401 errors (authentication required).</li>
 *   <li>{@link ForbiddenException} - For HTTP 403 errors (access denied).</li>
 *   <li>{@link NotFoundException} - For HTTP 404 errors (resource not found).</li>
 *   <li>{@link ConflictException} - For HTTP 409 errors (conflicting resources).</li>
 *   <li>{@link InternalServerException} - For HTTP 500 errors (server-side issues).</li>
 * </ul>
 */
@Getter
public abstract class ApplicationException extends RuntimeException {

    private final HttpStatus status;

    public ApplicationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApplicationException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

}
