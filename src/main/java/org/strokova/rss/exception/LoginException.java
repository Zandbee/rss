package org.strokova.rss.exception;

/**
 * author: Veronika, 8/17/2016.
 */
public class LoginException extends RuntimeException {

    private static final String DEFAULT_EXCEPTION_MSG = "Login failed";

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
