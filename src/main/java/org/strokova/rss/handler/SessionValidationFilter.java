package org.strokova.rss.handler;

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
        if (!((HttpServletRequest) request).getRequestURI().startsWith("/rss/login")
                && !((HttpServletRequest) request).getRequestURI().startsWith("/rss/registration")) {
            HttpSession session = ((HttpServletRequest) request).getSession(false);
            if (session == null || session.getAttribute(LoginServlet.SESSION_ATTRIBUTE_USER_ID) == null) {
                ((HttpServletResponse) response).sendRedirect("login.jsp");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
