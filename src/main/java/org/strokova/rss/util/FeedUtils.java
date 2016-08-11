package org.strokova.rss.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 11.08.2016.
 */
public final class FeedUtils {
    private static final Logger logger = Logger.getLogger(FeedUtils.class.getName());

    public static String encodeUrl(String url) {
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Error encoding URL", e);
        }
        return encodedUrl;
    }

    public static String decodeUrl(String url) {
        String decodedUrl = null;
        try {
            decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Error encoding URL", e);
        }
        return decodedUrl;
    }
}
