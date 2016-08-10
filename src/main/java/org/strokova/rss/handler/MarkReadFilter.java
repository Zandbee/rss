package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 10.08.2016.
 */
@WebFilter({"/latest.jsp", "/feed.jsp"})
public class MarkReadFilter implements Filter {
    private static final Logger logger = Logger.getLogger(MarkReadFilter.class.getName());

    private static final String PARAM_MARK_READ = "markRead";
    private static final String ATTR_USER_ID = "userId";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String guid = request.getParameter(PARAM_MARK_READ);
        if (guid != null) {
            try {
                FeedDbUtils.updateItemReadStatus(
                        (int) ((HttpServletRequest) request).getSession().getAttribute(ATTR_USER_ID),
                        guid);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error processing SQL", e);
            }
        }
        chain.doFilter(request, response);

    }
}
