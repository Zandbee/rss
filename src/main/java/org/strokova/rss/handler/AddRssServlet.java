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
 * Created by vstrokova on 03.08.2016.
 */
@WebServlet
public class AddRssServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AddRssServlet.class.getName());

    private static final String PARAM_RSS_LINK = "rss_link";
    private static final String PARAM_RSS_NAME = "rss_name";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String rssLink = request.getParameter(PARAM_RSS_LINK);
        String rssName = request.getParameter(PARAM_RSS_NAME);

        try {
            FeedDAO.addRssForUser(rssLink, rssName, (int) request.getSession(false).getAttribute("userId"));
            response.sendRedirect("latest.jsp");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error processing feed", e);
        }
    }
}
