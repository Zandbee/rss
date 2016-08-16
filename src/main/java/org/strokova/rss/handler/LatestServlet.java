package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String page = req.getParameter(PARAM_PAGE);
        int pageNum = page == null ? 0 : Integer.parseInt(page);
        List<FeedItemWithReadStatus> feedItems = FeedDAO.getUserFeedItemsLatestPage(
                (int) req.getSession(false).getAttribute(SESSION_ATTR_USER_ID),
                pageNum,
                req.getParameter(PARAM_ORDER));
        req.setAttribute(REQ_ATTR_FEED_ITEMS, feedItems);

        req.getRequestDispatcher("latest.jsp").forward(req, resp);
    }
}
