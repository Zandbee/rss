package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDbUtils;
import org.strokova.rss.exception.MarkReadRuntimeException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vstrokova, 15.08.2016.
 */
@WebServlet("/markRead")
public class MarkReadServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(MarkReadServlet.class.getName());

    private static final String HEADER_REFERRER = "referer";
    private static final String PARAM_MARK_READ = "markRead";
    private static final String ATTR_USER_ID = "userId";
    private static final String MARK_READ_EXCEPTION_MSG = "Could not mark as read";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String guid = req.getParameter(PARAM_MARK_READ);
        if (guid != null) {
            try {
                FeedDbUtils.updateItemReadStatus((int) req.getSession().getAttribute(ATTR_USER_ID), guid);
                // redirect back
                resp.sendRedirect(req.getHeader(HEADER_REFERRER));
            } catch (SQLException e) {
                logger.log(Level.SEVERE, MARK_READ_EXCEPTION_MSG, e);
                throw new MarkReadRuntimeException(MARK_READ_EXCEPTION_MSG, e);
            }
        }
    }
}
