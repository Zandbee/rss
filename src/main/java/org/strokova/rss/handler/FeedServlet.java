package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.exception.FeedPageRuntimeException;
import org.strokova.rss.obj.FeedItemWithReadStatus;
import org.strokova.rss.obj.SubscriptionWithFeed;
import org.strokova.rss.util.FeedUtils;

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

    private static final String PARAM_RENAME_LINK = "rename";
    private static final String PARAM_NEW_FEED_NAME = "newFeedName";
    private static final String PARAM_REMOVE_LINK = "remove";
    private static final String PARAM_RSS_ID = "id";
    private static final String PARAM_PAGE = "page";
    private static final String PARAM_ORDER = "order";
    private static final String SESSION_ATTR_USER_ID = "userId";
    private static final String REQ_ATTR_FEED = "feed";
    private static final String REQ_ATTR_FEED_ITEMS = "feedItems";
    private static final String REQ_ATTR_PAGINATION_PAGE_COUNT = "pageCount";
    private static final String REQ_ATTR_PAGINATION_SERVLET_PATTERN = "servletPattern";
    private static final String REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE = "feed";
    private static final String REQ_ATTR_RSSLIST_SUBSCRIPTIONS = "subscriptions";
    private static final String FEED_PAGE_EXCEPTION_MSG = "Cannot update the Feed page";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String feedLink = req.getParameter(PARAM_RSS_ID);
        int userId = (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID);

        try {
            setupItemsList(req, userId, feedLink);
            setupRssList(req, userId);
            setupPagination(req, userId, feedLink);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, FEED_PAGE_EXCEPTION_MSG, e);
            throw new FeedPageRuntimeException(FEED_PAGE_EXCEPTION_MSG, e);
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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, FEED_PAGE_EXCEPTION_MSG, e);
            throw new FeedPageRuntimeException(FEED_PAGE_EXCEPTION_MSG, e);
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
        req.setAttribute(REQ_ATTR_PAGINATION_SERVLET_PATTERN, REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE);
    }

    private static void setupRssList(HttpServletRequest req, int userId) throws SQLException {
        req.setAttribute(REQ_ATTR_RSSLIST_SUBSCRIPTIONS, FeedDbUtils.getUserSubscriptionsWithFeeds(userId));
    }

    private static void renameRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) throws ServletException, SQLException, IOException {
        String newFeedName = req.getParameter(PARAM_NEW_FEED_NAME);
        FeedDbUtils.updateSubscriptionInSubscriptionTable(
                (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID),
                FeedUtils.decodeUrl(feedLink), newFeedName);
        //req.getRequestDispatcher(req.getServletPath()).forward(req, resp);
        resp.sendRedirect("feed?id=" + feedLink); // TODO: fix
    }

    private static void removeRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
            FeedDAO.deleteRssForUser(
                    FeedUtils.decodeUrl(feedLink),
                    (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID));
            resp.sendRedirect("latest");
    }
}
