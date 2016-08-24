package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.exception.LatestPageException;
import org.strokova.rss.obj.FeedItem;
import org.strokova.rss.obj.FeedItemWithReadStatus;
import org.strokova.rss.obj.Subscription;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 15.08.2016.
 */
@WebServlet("/latest")
public class LatestServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LatestServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession(false).getAttribute(SESSION_ATTR_USER_ID);

        try {
            setupItemsList(req, userId);
            setupRssList(req, userId);
            setupPagination(req, userId);
            setupMarkReadButton(req);

            req.getRequestDispatcher("/WEB-INF/jsp/latest.jsp").forward(req, resp);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error on Latest page", e);
            throw new LatestPageException(e);
        }
    }

    private static void setupItemsList(HttpServletRequest req, int userId) throws SQLException {
        String page = req.getParameter(PARAM_PAGE);
        int pageNum = page == null ? 0 : Integer.parseInt(page);
        List<FeedItemWithReadStatus> feedItems = FeedDAO.getUserFeedItemsLatestPage(
                userId,
                pageNum,
                req.getParameter(PARAM_ORDER));
        req.setAttribute(REQ_ATTR_FEED_ITEMS, feedItems);
    }

    private static void setupPagination(HttpServletRequest req, int userId) throws SQLException {
        req.setAttribute(REQ_ATTR_PAGINATION_PAGE_COUNT, FeedDAO.getPageCountInLatest(userId));
        req.setAttribute(REQ_ATTR_PAGINATION_SERVLET_PATTERN, REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE_LATEST);
    }

    private static void setupRssList(HttpServletRequest req, int userId) throws SQLException {
        req.setAttribute(REQ_ATTR_RSSLIST_SUBSCRIPTIONS, FeedDbUtils.getUserSubscriptionsWithFeeds(userId));
        req.setAttribute(REQ_ATTR_MAX_LENGTH_FEED_LINK, FeedItem.COLUMN_LINK_LENGTH);
        req.setAttribute(REQ_ATTR_MAX_LENGTH_FEED_NAME, Subscription.COLUMN_FEED_NAME_LENGTH);
    }

    private static void setupMarkReadButton(HttpServletRequest req) {
        String requestQueryString = req.getQueryString();
        req.setAttribute(REQ_ATTR_REDIRECT_URI, requestQueryString == null ? "latest" : "latest?" + requestQueryString);
    }
}
