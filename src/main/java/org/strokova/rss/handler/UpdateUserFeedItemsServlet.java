package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import com.rometools.rome.io.FeedException;
import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.exception.UpdateFeedsException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 09.08.2016.
 */
@WebServlet("/updateUserFeedItems")
public class UpdateUserFeedItemsServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(UpdateUserFeedItemsServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            FeedDAO.updateRssItemsForUser((int) req.getSession(false).getAttribute(SESSION_ATTR_USER_ID));
            resp.sendRedirect("latest");
        } catch (SQLException | FeedException e) {
            logger.log(Level.SEVERE, "Error updating user's feed items", e);
            throw new UpdateFeedsException(e);
        }
    }
}
