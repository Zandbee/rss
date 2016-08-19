package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.*;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * author: Veronika, 7/31/2016.
 */

// If no session, redirect user to login page
@WebFilter("/*")
public class SessionValidationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // filter should work for any page, except for login, registration, and error
        // in order not to enumerate all the existing servlets in pattern, these three are excluded using if
        if (!((HttpServletRequest) request).getRequestURI().startsWith("/rss/login")
                && !((HttpServletRequest) request).getRequestURI().startsWith("/rss/registration")
                && !((HttpServletRequest) request).getRequestURI().toLowerCase().contains("error")) {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session == null || session.getAttribute(SESSION_ATTR_USER_ID) == null) {
                ((HttpServletResponse) response).sendRedirect("login");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
