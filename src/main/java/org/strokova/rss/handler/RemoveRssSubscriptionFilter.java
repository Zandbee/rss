package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author: Veronika, 8/8/2016.
 */
// TODO: use annotations instead of web.xml
@WebFilter("/feed.jsp")
public class RemoveRssSubscriptionFilter implements Filter {

    private static final String PARAM_REMOVE_LINK = "remove";
    private static final String SESSION_ATTR_USER_ID = "userId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String feedLink = request.getParameter(PARAM_REMOVE_LINK);
        if (feedLink != null) {
            int userId = (int) ((HttpServletRequest) request).getSession().getAttribute(SESSION_ATTR_USER_ID);
            FeedDbUtils.deleteFromSubscriptionTable(userId, feedLink);
            ((HttpServletResponse) response).sendRedirect("latest.jsp");
        }
        chain.doFilter(request, response);
    }
}
