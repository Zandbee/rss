package org.strokova.rss.exception;

/**
 * author: Veronika, 8/17/2016.
 */
public class MarkReadException extends RuntimeException {

    private static final String DEFAULT_EXCEPTION_MSG = "Could not mark as read";

    public MarkReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public MarkReadException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
