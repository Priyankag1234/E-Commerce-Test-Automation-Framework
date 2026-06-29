package com.ecommerce.framework.exceptions;

/**
 * FrameworkException — Root unchecked exception for all framework-level failures.
 *
 * <p>Extends RuntimeException so callers are not forced to declare checked exceptions.
 * <p>Provides meaningful, context-rich error messages instead of generic stack traces.
 */
public class FrameworkException extends RuntimeException {

    /**
     * Constructs a FrameworkException with a detail message.
     *
     * @param message descriptive error message
     */
    public FrameworkException(String message) {
        super(message);
    }

    /**
     * Constructs a FrameworkException with a detail message and a cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying exception
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
