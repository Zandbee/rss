package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.util.FeedUtils;

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
 * @author vstrokova, 15.08.2016.
 */
@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FeedServlet.class.getName());


    private static final String PARAM_RENAME_LINK = "rename";
    private static final String PARAM_NEW_FEED_NAME = "newFeedName";
    private static final String PARAM_REMOVE_LINK = "remove";
    private static final String PARAM_RSS_ID = "id";
    private static final String SESSION_ATTR_USER_ID = "userId";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/feed.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String renameFeedLink = req.getParameter(PARAM_RENAME_LINK);
        if (renameFeedLink != null) {
            renameRss(renameFeedLink, req, resp);
        }

        String removeFeedLink = req.getParameter(PARAM_REMOVE_LINK);
        if (removeFeedLink != null) {
            removeRss(removeFeedLink, req, resp);
        }
    }

    private static void renameRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            String newFeedName = req.getParameter(PARAM_NEW_FEED_NAME);
            FeedDbUtils.renameSubscriptionInSubscriptionTable(
                    (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID),
                    FeedUtils.decodeUrl(feedLink), newFeedName);
            //req.getRequestDispatcher(req.getServletPath()).forward(req, resp);
            resp.sendRedirect("feed.jsp?id=" + req.getParameter(PARAM_RSS_ID)); // TODO: fix
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error removing RSS", e);
        }
    }

    private static void removeRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) {
        try {
            FeedDAO.deleteRssForUser(
                    FeedUtils.decodeUrl(feedLink),
                    (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID));
            resp.sendRedirect("latest.jsp");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error removing RSS", e);
        }
    }
}
