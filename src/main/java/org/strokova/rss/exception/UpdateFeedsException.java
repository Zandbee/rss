package org.strokova.rss.exception;

/**
 * author: Veronika, 8/17/2016.
 */
public class UpdateFeedsException extends RuntimeException {

    private static final String DEFAULT_EXCEPTION_MSG = "Could not update feeds";

    public UpdateFeedsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateFeedsException(Throwable cause) {
        super(DEFAULT_EXCEPTION_MSG, cause);
    }
}
