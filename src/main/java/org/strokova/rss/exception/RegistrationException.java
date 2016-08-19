package org.strokova.rss.exception;

/**
 * author: Veronika, 8/17/2016.
 */
public class RegistrationException extends RuntimeException {

    private static final String DEFAULT_EXCEPTION_MSG = "Registration failed";

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
