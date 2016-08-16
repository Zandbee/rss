package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.obj.FeedItemWithReadStatus;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author vstrokova, 15.08.2016.
 */
@WebServlet("/latest")
public class LatestServlet extends HttpServlet {

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_ORDER = "order";
    private static final String SESSION_ATTR_USER_ID = "userId";
    private static final String REQ_ATTR_FEED_ITEMS = "feedItems";
    private static final String REQ_ATTR_PAGINATION_PAGE_COUNT = "pageCount";
    private static final String REQ_ATTR_PAGINATION_SERVLET_PATTERN = "servletPattern";
    private static final String REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE = "latest";
    private static final String REQ_ATTR_RSSLIST_SUBSCRIPTIONS = "subscriptions";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int userId = (int) req.getSession(false).getAttribute(SESSION_ATTR_USER_ID);

        setupItemsList(req, userId);
        setupRssList(req, userId);
        setupPagination(req, userId);

        req.getRequestDispatcher("latest.jsp").forward(req, resp);
    }

    private static void setupItemsList(HttpServletRequest req, int userId) {
        String page = req.getParameter(PARAM_PAGE);
        int pageNum = page == null ? 0 : Integer.parseInt(page);
        List<FeedItemWithReadStatus> feedItems = FeedDAO.getUserFeedItemsLatestPage(
                userId,
                pageNum,
                req.getParameter(PARAM_ORDER));
        req.setAttribute(REQ_ATTR_FEED_ITEMS, feedItems);
    }

    private static void setupPagination(HttpServletRequest req, int userId) {
        req.setAttribute(REQ_ATTR_PAGINATION_PAGE_COUNT, FeedDAO.getPageCountInLatest(userId));
        req.setAttribute(REQ_ATTR_PAGINATION_SERVLET_PATTERN, REQ_ATTR_PAGINATION_SERVLET_PATTERN_VALUE);
    }

    private static void setupRssList(HttpServletRequest req, int userId) {
        req.setAttribute(REQ_ATTR_RSSLIST_SUBSCRIPTIONS, FeedDbUtils.getUserSubscriptionsWithFeeds(userId));
    }
}
