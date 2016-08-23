package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.exception.FeedPageException;
import org.strokova.rss.exception.ValidationFailedException;
import org.strokova.rss.obj.FeedItem;
import org.strokova.rss.obj.FeedItemWithReadStatus;
import org.strokova.rss.obj.Subscription;
import org.strokova.rss.obj.SubscriptionWithFeed;
import org.strokova.rss.util.Utils;

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
@WebServlet("/feed")
public class FeedServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(FeedServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String feedLink = req.getParameter(PARAM_RSS_ID);
        int userId = (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID);

        try {
            setupItemsList(req, userId, feedLink);
            setupRssList(req, userId);
            setupPagination(req, userId, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error on Feed page", e);
            throw new FeedPageException(e);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/feed.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String renameFeedLink = req.getParameter(PARAM_RENAME_LINK);
            if (renameFeedLink != null) {
                renameRss(renameFeedLink, req, resp);
            }

            String removeFeedLink = req.getParameter(PARAM_REMOVE_LINK);
            if (removeFeedLink != null) {
                removeRss(removeFeedLink, req, resp);
            }
        } catch (SQLException | ValidationFailedException e) {
            logger.log(Level.SEVERE, "Error on Feed page", e);
            throw new FeedPageException(e);
        }
    }

    private static void setupItemsList(HttpServletRequest req, int userId, String feedLink) throws SQLException {
        SubscriptionWithFeed feed = FeedDbUtils.getSubscriptionWithFeedByFeedLink(feedLink, userId);
        req.setAttribute(REQ_ATTR_FEED, feed);

        String page = req.getParameter(PARAM_PAGE);
        int pageNum = page == null ? 0 : Integer.parseInt(page);
        List<FeedItemWithReadStatus> feedItems = FeedDAO.getFeedItemsByFeedLinkPage(
                userId,
                feedLink,
                pageNum,
                req.getParameter(PARAM_ORDER));
        req.setAttribute(REQ_ATTR_FEED_ITEMS, feedItems);
    }

    private static void setupPagination(HttpServletRequest req, int userId, String feedLink) throws SQLException {
        req.setAttribute(REQ_ATTR_PAGINATION_PAGE_COUNT, FeedDAO.getPageCountByFeedLink(feedLink, userId));
        req.setAttribute(REQ_ATTR_PAGINATION_SERVLET_PATTERN, REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE_FEED);
    }

    private static void setupRssList(HttpServletRequest req, int userId) throws SQLException {
        req.setAttribute(REQ_ATTR_RSSLIST_SUBSCRIPTIONS, FeedDbUtils.getUserSubscriptionsWithFeeds(userId));
        req.setAttribute(REQ_ATTR_MAX_LENGTH_FEED_LINK, FeedItem.COLUMN_LINK_LENGTH);
        req.setAttribute(REQ_ATTR_MAX_LENGTH_FEED_NAME, Subscription.COLUMN_FEED_NAME_LENGTH);
    }

    private static void renameRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException, IOException {
        String newFeedName = req.getParameter(PARAM_NEW_FEED_NAME);
        FeedDbUtils.updateSubscriptionInSubscriptionTable(
                (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID),
                Utils.decodeUrl(feedLink), newFeedName);
        resp.sendRedirect("feed?id=" + feedLink);
    }

    private static void removeRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException, ValidationFailedException {
            FeedDAO.deleteRssForUser(
                    Utils.decodeUrl(feedLink),
                    (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID));
            resp.sendRedirect("latest");
    }
}
