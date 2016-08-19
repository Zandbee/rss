package org.strokova.rss.exception;

/**
 * author: Veronika, 8/17/2016.
 */
public class NewRssException extends RuntimeException {

    private static final String DEFAULT_EXCEPTION_MSG = "Could not add a new RSS";

    public NewRssException(String message, Throwable cause) {
        super(message, cause);
    }

    public NewRssException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
