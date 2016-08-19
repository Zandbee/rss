package org.strokova.rss.exception;

/**
 * author: Veronika, 8/17/2016.
 */
public class LatestPageException extends RuntimeException {
    private static final String DEFAULT_EXCEPTION_MSG = "Cannot update the Latest page";

    public LatestPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public LatestPageException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
