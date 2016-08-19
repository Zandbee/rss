package org.strokova.rss.exception;

/**
 * @author vstrokova, 18.08.2016.
 */
public class FeedPageException extends RuntimeException {
    private static final String DEFAULT_EXCEPTION_MSG = "Cannot update the Feed page";

    public FeedPageException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeedPageException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
