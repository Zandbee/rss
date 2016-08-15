package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;
import org.strokova.rss.database.FeedDbDataSource;
import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.util.FeedUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author: Veronika, 8/8/2016.
 */
// TODO: use annotations instead of web.xml
//@WebFilter("/feed.jsp")
public class RemoveRssSubscriptionFilter implements Filter {
    private static final Logger logger = Logger.getLogger(RemoveRssSubscriptionFilter.class.getName());

    private static final String PARAM_REMOVE_LINK = "remove";
    private static final String SESSION_ATTR_USER_ID = "userId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String feedLink = request.getParameter(PARAM_REMOVE_LINK);
        if (feedLink != null) {
            try {
                FeedDAO.deleteRssForUser(FeedUtils.decodeUrl(feedLink), (int) ((HttpServletRequest) request).getSession().getAttribute(SESSION_ATTR_USER_ID));
                ((HttpServletResponse) response).sendRedirect("latest.jsp");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error processing feed", e);
            }
        }
        chain.doFilter(request, response);
    }
}
