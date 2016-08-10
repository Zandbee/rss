package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author vstrokova, 09.08.2016.
 */
@WebFilter("/feed.jsp")
public class RenameRssSubscriptionFilter implements Filter {

    private static final String PARAM_RENAME_LINK = "rename";
    private static final String PARAM_NEW_FEED_NAME = "newFeedName";
    private static final String SESSION_ATTR_USER_ID = "userId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String feedLink = request.getParameter(PARAM_RENAME_LINK);
        if (feedLink != null) {
            String newFeedName = request.getParameter(PARAM_NEW_FEED_NAME);
            int userId = (int) ((HttpServletRequest) request).getSession().getAttribute(SESSION_ATTR_USER_ID);
            FeedDbUtils.renameSubscriptionInSubscriptionTable(userId, feedLink, newFeedName);
            ((HttpServletResponse) response).sendRedirect("feed.jsp?id=" + request.getParameter("id"));
        }
        chain.doFilter(request, response);
    }
}
