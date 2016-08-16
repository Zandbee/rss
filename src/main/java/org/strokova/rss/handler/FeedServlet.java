package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbUtils;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String feedLink = req.getParameter(PARAM_RSS_ID);
        int userId = (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID);

        setupItemsList(req, userId, feedLink);
        setupRssList(req, userId);
        setupPagination(req, userId, feedLink);

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

    private static void setupItemsList(HttpServletRequest req, int userId, String feedLink) {
        SubscriptionWithFeed feed = FeedDbUtils.getSubscriptionWithFeedByFeedLink(feedLink);
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

    private static void setupPagination(HttpServletRequest req, int userId, String feedLink) {
        req.setAttribute(REQ_ATTR_PAGINATION_PAGE_COUNT, FeedDAO.getPageCountByFeedLink(feedLink, userId));
        req.setAttribute(REQ_ATTR_PAGINATION_SERVLET_PATTERN, REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE);
    }

    private static void setupRssList(HttpServletRequest req, int userId) {
        req.setAttribute(REQ_ATTR_RSSLIST_SUBSCRIPTIONS, FeedDbUtils.getUserSubscriptionsWithFeeds(userId));
    }

    private static void renameRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        try {
            String newFeedName = req.getParameter(PARAM_NEW_FEED_NAME);
            FeedDbUtils.renameSubscriptionInSubscriptionTable(
                    (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID),
                    FeedUtils.decodeUrl(feedLink), newFeedName);
            //req.getRequestDispatcher(req.getServletPath()).forward(req, resp);
            resp.sendRedirect("feed?id=" + feedLink); // TODO: fix
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error removing RSS", e);
        }
    }

    private static void removeRss(String feedLink, HttpServletRequest req, HttpServletResponse resp) {
        try {
            FeedDAO.deleteRssForUser(
                    FeedUtils.decodeUrl(feedLink),
                    (int) req.getSession().getAttribute(SESSION_ATTR_USER_ID));
            resp.sendRedirect("latest");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing SQL", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error removing RSS", e);
        }
    }
}
