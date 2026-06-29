package com.ecommerce.framework.exceptions;

/**
 * ElementNotFoundException — Thrown when a UI element cannot be located within the wait timeout.
 *
 * <p>Provides richer context than Selenium's NoSuchElementException by including
 *    the locator strategy, page context, and timeout duration in the message.
 */
public class ElementNotFoundException extends FrameworkException {

    /**
     * Constructs an ElementNotFoundException with a descriptive message.
     *
     * @param message the error message including locator and context details
     */
    public ElementNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs an ElementNotFoundException with a message and the originating cause.
     *
     * @param message descriptive error message
     * @param cause   the underlying Selenium or timeout exception
     */
    public ElementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
