package org.strokova.rss.handler;

import static org.strokova.rss.util.RequestConstants.ENCODING_UTF_8;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author vstrokova, 19.08.2016.
 */
@WebFilter("/*")
public class SetupRequestEncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        chain.doFilter(request, response);
    }
}
