package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            FeedDAO.updateRssItemsForUser((int) req.getSession(false).getAttribute(ATTR_USER_ID));
            resp.sendRedirect("latest");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error processing feed", e);
        }
    }
}
