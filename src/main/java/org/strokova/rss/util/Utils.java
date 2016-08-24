package org.strokova.rss.util;

import static org.strokova.rss.util.RequestConstants.ENCODING_UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 11.08.2016.
 */
public final class Utils {
    private static final Logger logger = Logger.getLogger(Utils.class.getName());

    private static final String HASH_ALGORITHM = "SHA-1";
    private static MessageDigest md;
    static {
        try {
            md = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Cannot hash password", e);
        }
    }

    private Utils() {}

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

    public static String hashPassword(String password) {
        try {
            md.update(password.getBytes(ENCODING_UTF_8));
            byte[] bytes = md.digest();
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, "Cannot hash password", e);
            throw new RuntimeException(e);
        }
    }
}
