package org.strokova.rss.handler;

import com.rometools.rome.io.FeedException;
import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.exception.UpdateFeedsRuntimeException;

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

    private static final String ATTR_USER_ID = "userId";
    private static final String UPDATE_FEEDS_EXCEPTION_MSG = "Could not update feeds";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            FeedDAO.updateRssItemsForUser((int) req.getSession(false).getAttribute(ATTR_USER_ID));
            resp.sendRedirect("latest");
        } catch (SQLException | FeedException e) {
            logger.log(Level.SEVERE, UPDATE_FEEDS_EXCEPTION_MSG, e);
            throw new UpdateFeedsRuntimeException(UPDATE_FEEDS_EXCEPTION_MSG, e);
        }
    }
}
