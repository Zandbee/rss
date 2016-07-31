package org.strokova.rss.handler;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * author: Veronika, 7/31/2016.
 */

//Invalidate user's session if they explicitly go to login page
@WebFilter("/login.jsp")
public class LoginSessionInvalidationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpSession session = ((HttpServletRequest) request).getSession(false);
        if (session != null) {
            session.invalidate();
        }
        chain.doFilter(request, response);
    }
}
