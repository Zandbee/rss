package org.strokova.rss.handler;

import org.strokova.rss.database.FeedDAO;

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
 * @author vstrokova, 03.08.2016.
 */
@WebServlet("/addRss")
public class AddRssServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AddRssServlet.class.getName());

    private static final String PARAM_RSS_LINK = "rss_link";
    private static final String PARAM_RSS_NAME = "rss_name";
    private static final String ATTR_USER_ID = "userId";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String rssLink = request.getParameter(PARAM_RSS_LINK);
        String rssName = request.getParameter(PARAM_RSS_NAME);

        try {
            FeedDAO.addRssForUser(rssLink, rssName, (int) request.getSession(false).getAttribute(ATTR_USER_ID));
            response.sendRedirect("latest");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error processing feed", e);
        }
    }
}
